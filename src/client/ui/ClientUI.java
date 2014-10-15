package client.ui;

import client.BattleClient;
import client.BattleClientGuiInterface;
import global.Message;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class ClientUI extends JFrame implements BattleClientGuiInterface {
    //this is where the messagesPane you send and receive are shown
    JTextPane messagesPane;
    //The client handles all communication with server
    BattleClient client;
    //usernames
    String opponentUsername;
    String username;

    //contains the messagesPane text area and the input text field
    JPanel chattingArea = new JPanel(new BorderLayout());
    //the text area is wrapped in a scroll pane
    JScrollPane pane = new JScrollPane();


    //a list of all messages
    ArrayList<String> Messages = new ArrayList<String>();


    //the current player can be either ME or OPPONENT , 0 is unset
    public int currentPlayer = 0;
    public static final int ME=1;
    public static final int OPPONENT=2;

    //the two boards
    BattleBoardLocal userBoard = new BattleBoardLocal();
    BattleBoardOpponent boardOpponent = new BattleBoardOpponent();

    public ClientUI(){


        this.setSize(750, 600);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*CountdownClock clock = new CountdownClock(30);
        clock.setSize(300,300);
        clock.setOpaque(false);
        this.add(clock);*/

        setLayout(new BorderLayout());
        username = JOptionPane.showInputDialog(this,"Enter a username");

        //the client uses while so it must run on a separate thread
        (new Thread() {
            public void run() {

                client = new BattleClient("localhost" , 9001 , username , ClientUI.this);
                try {
                    client.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        setTitle(username + " playing");

        //dummy container for the actual board
        Border margin = new EmptyBorder(10,10,10,10);
        CompoundBorder compoundBorder = new CompoundBorder(new LineBorder(Color.darkGray , 1), margin);

        JPanel inner = new JPanel(new GridLayout(0,2,10,10));
        add(inner , BorderLayout.CENTER);

        JPanel leftPanel = new JPanel(new FlowLayout());

        userBoard.addShipPlacementListener(new BattleBoardLocal.ShipPlacementListener() {
            @Override
            public void onFinished() {
                try {
                    //tell the other user , you're ready
                    client.sendMessage(new Message(opponentUsername,Message.READY_TO_PLAY));
                    //if the other opponent hasn't finished ,you go first
                   currentPlayer = ME;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        leftPanel.add(userBoard);
        inner.add(leftPanel);

        //TODO finish this clock thing
        JPanel clockContainer = new JPanel( new FlowLayout());
        CountdownClock clock = new CountdownClock(60);
        clock.setMaximumSize(new Dimension(50,50));
        clock.setPreferredSize(new Dimension(50, 50));
        clockContainer.add(clock);
        //inner.add(clockContainer);

        JPanel rightPanel = new JPanel(new FlowLayout());

        boardOpponent.addShotListener(new BattleBoardOpponent.ShotListener() {
			@Override
			public void onShotFired(int x, int y) {
				//you must be the current player to shoot
				if (currentPlayer == ME) {
					try {
						client.sendMessage(new Message(opponentUsername, Message.SHOOT, x, y));
						//after shooting it's their turn
						currentPlayer = OPPONENT;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
        rightPanel.add(boardOpponent);
        inner.add(rightPanel);




        chattingArea.setSize(new Dimension(0, 70));
        chattingArea.setPreferredSize(new Dimension(0, 150));
        add(chattingArea, BorderLayout.SOUTH);

        messagesPane = new JTextPane();
        messagesPane.setContentType("text/html");

        pane = new JScrollPane(messagesPane);
        chattingArea.add(pane, BorderLayout.CENTER);

        final JTextField input = new JTextField();
        chattingArea.add(input, BorderLayout.SOUTH);

        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    //when the user presses enter send message
                    Message toSend = new Message(opponentUsername, Message.CHAT_MESSAGE, input.getText().trim());
                    try {
                        client.sendMessage(toSend);
                        appendToLog( "\n" + username + ": " + input.getText());
                        input.setText("");
                    } catch (IOException ex) {
                        System.out.println("# Failed to send message");
                        ex.printStackTrace();
                    }

            }
        });


        this.setVisible(true);
    }

    @Override
    public void onReceiveMessage(Message msg) {
        //if the opponent is set change the title bar text and save it for chatting , else add message to text area
        if(msg == null) return;
        switch (msg.getType()){
            case Message.SET_OPPONENT:{
                if(msg.getMessage() == null)return;
                opponentUsername = msg.getMessage();
                setTitle(username +" playing against " + opponentUsername);
                break;
            }
            case Message.CHAT_MESSAGE:{
                if(msg.getMessage() == null)return;
                appendToLog( "\n<b>" + opponentUsername + "</b>: " + msg.getMessage());
                break;
            }
            case Message.OPPONENT_DISCONNECTED:{
                System.exit(0);
                break;
            }
            case Message.READY_TO_PLAY:{
                currentPlayer = OPPONENT;
                appendToLog( "\n<b>" + opponentUsername + " has placed his ships and is ready to play</b>");
                break;
            }

            case Message.SHOOT:{
                //you've been shot at , so it's your turn
                currentPlayer = ME;
                System.out.println("Being shot at "+ msg.getX() +" "+msg.getY());
                //find the tile they hit
                BattleAnimationPanel hitAt = userBoard.getTiles()[msg.getX()][msg.getY()];
                //if it's empty it's a miss
                //notify the other user of which it is
                if (hitAt.object.getIcon() !=null){
                    hitAt.setAsHitPin();
                    try {
                        client.sendMessage(new Message(opponentUsername, Message.HIT,msg.getX(),msg.getY()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    hitAt.setAsMissPin();
                    try {
                        client.sendMessage(new Message(opponentUsername, Message.MISS,msg.getX(),msg.getY()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }

            //this is triggered when the opponent confirms whether you hit or missed them
            case Message.HIT:{
                BattleAnimationPanel hitAt = boardOpponent.getBoardTiles()[msg.getX()][msg.getY()];
                hitAt.setAsHitPin();
                break;
            }
            case Message.MISS:{
                BattleAnimationPanel hitAt = boardOpponent.getBoardTiles()[msg.getX()][msg.getY()];
                hitAt.setAsMissPin();
                break;
            }
        }
    }

    public void appendToLog(String s) {
        /*try {
            Document doc = messagesPane.getDocument();
            doc.insertString(doc.getLength(), s, null);
        } catch(Exception exc) {
            exc.printStackTrace();
        }*/

        Messages.add(s);

        //this code appends a line as hmtl
        messagesPane.setContentType( "text/html" );
        messagesPane.setEditable(false);
        HTMLDocument doc = (HTMLDocument)messagesPane.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit)messagesPane.getEditorKit();
        String text = s;
        try {
            editorKit.insertHTML(doc, doc.getLength(), text, 0, 0, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
