package client.ui;

import client.BattleClient;
import client.BattleClientGuiInterface;
import global.Message;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientUI extends JFrame implements BattleClientGuiInterface {
    //this is where the messages you send and receive are shown
    JTextArea messages;
    //The client handles all communication with server
    BattleClient client;
    //usernames
    String opponentUsername;
    String username;

    //contains the messages text area and the input text field
    JPanel chattingArea = new JPanel(new BorderLayout());
    //the text area is wrapped in a scroll pane
    JScrollPane pane = new JScrollPane();


    public ClientUI(){


        this.setSize(600, 500);
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

        JPanel inner = new JPanel(new GridLayout(0,3,10,10));
        add(inner , BorderLayout.CENTER);

        JPanel leftPanel = new JPanel();
        leftPanel.setBorder(compoundBorder);
        inner.add(leftPanel);

        JPanel clockContainer = new JPanel( new FlowLayout());
        CountdownClock clock = new CountdownClock(60);
        clock.setMaximumSize(new Dimension(100,100));
        clock.setPreferredSize(new Dimension(100, 100));
        clockContainer.add(clock);
        inner.add(clockContainer);

        JPanel rightPanel = new JPanel();
        rightPanel.setBorder(compoundBorder);
        inner.add(rightPanel);




        chattingArea.setSize(new Dimension(0, 70));
        chattingArea.setPreferredSize(new Dimension(0, 150));
        add(chattingArea, BorderLayout.SOUTH);

        messages= new JTextArea();
        pane = new JScrollPane(messages);
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
                        messages.append("\n" + username +": "+ input.getText());
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
                messages.append("\n" + opponentUsername +": "+ msg.getMessage());
                break;
            }
            case Message.OPPONENT_DISCONNECTED:{
                System.exit(0);
                break;
            }
        }
    }
}
