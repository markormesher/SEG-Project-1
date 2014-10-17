package client;

import client.ui_components.*;
import client.ui_components.BattleBoardLocal.ShipPlacementListener;
import global.Message;
import global.Settings;

import javax.imageio.ImageIO;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

// TODO: add listener for messages from the server (add them to the chat console?)

public class BattleClientGui extends JFrame implements BattleClientGuiInterface {


    //the 8bit font
    private Font font;
    //used for when the clock runs out
    private Random random = new Random();
	// the client handles all communication with server
	private BattleClient client;

	// usernames
	private String opponentUsername;
	private String playerUsername;

	// the current player can be either ME or OPPONENT, 0 is unset
	public int currentPlayer = 0;
	public static final int ME = 1;
	public static final int OPPONENT = 2;

	// are both sides ready yet?
	private boolean opponentReady = false;
	private boolean playerReady = false;

	// a list of all messages
	private ArrayList<String> messages = new ArrayList<String>();

	// messaging output
	private JTextPane messagesPane;

    //the label that displays the user the current status
    private JLabel statusLabel;

    private CountdownClock clock;

	// the two boards
	private BattleBoardLocal localBoard = new BattleBoardLocal();
	private BattleBoardOpponent opponentBoard = new BattleBoardOpponent();

    private JLabel chatLabel;

    private BufferedImage waterTile;
	public BattleClientGui() {
        //loading the font from the ttf file
        try {
            font = Font.createFont(Font.TRUETYPE_FONT , getClass().getResourceAsStream("/PressStart2P.ttf"));
            font = font.deriveFont(25f);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // basic setup of frame
		setSize(Settings.GRID_SIZE * Settings.IMAGE_CELL_SIZE * 2 + Settings.IMAGE_CELL_SIZE*3, Settings.IMAGE_CELL_SIZE * 24);
        setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //The waterBackground is beneath the actual UI
        //The AnimationPanels no longer use a background but are transparent
        JPanel waterBackground = new JPanel(){
            public void paint(Graphics g){
                super.paint(g);
                if(waterTile == null){
                    try {
                        waterTile = ImageIO.read(this.getClass().getResource("/images/default/bg.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //cover the surface with water images
                for(int x =0;x < getWidth()/32.0;x++){
                    for(int y =0;y < getHeight()/32.0;y++){
                        g.drawImage(waterTile ,x*Settings.IMAGE_CELL_SIZE , y*Settings.IMAGE_CELL_SIZE, Settings.IMAGE_CELL_SIZE , Settings.IMAGE_CELL_SIZE,null);
                    }
                }
            }
        };

        waterBackground.setSize(new Dimension(getWidth(), getHeight()));
        waterBackground.setPreferredSize(new Dimension(getWidth(), getHeight()));
        getLayeredPane().add(waterBackground , new Integer(1));

        waterBackground.repaint();


        //this panel contains the actual ui and is overlayed on the water
        JPanel frameContent = new JPanel();
        frameContent.setLayout(new BorderLayout());
        frameContent.setOpaque(false);
        frameContent.setSize(new Dimension(getWidth(),getHeight()-18));
        getLayeredPane().add(frameContent, new Integer(100));

		// collect player username and set it as title
		playerUsername = JOptionPane.showInputDialog(this, "Enter a username");
		setTitle(playerUsername + " (you) vs. ???");

		// the client uses while so it must run on a separate thread
		(new Thread() {
			public void run() {
				client = new BattleClient(Settings.HOST_NAME, Settings.PORT_NUMBER, playerUsername, BattleClientGui.this);
				try {
					client.connect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();


        //the panel containing the battleship logo
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        ImageIcon title ;
        title = new ImageIcon(this.getClass().getResource("/images/logo.png"));
        topPanel.add(new JLabel(title) , BorderLayout.NORTH);

        //the panel with the clock
        JPanel clockPanel = new JPanel(new FlowLayout());
        clockPanel.setOpaque(false);
        clockPanel.setPreferredSize(new Dimension(150,138));
        clockPanel.setSize(new Dimension(150, 138));

        clock = new CountdownClock(30);
        clock.setSize(100,100);
        clock.setPreferredSize(new Dimension(100,100));
        clock.setOpaque(false);
        //triggered when the timer hits 0
        clock.addTimeoutListener(new CountdownClock.TimeoutListener() {
            @Override
            public void onTimeout() {
                if(currentPlayer == ME){
                    boolean shot = false;
                    while(!shot){
                        //find random tile
                        int x = random.nextInt(10);
                        int y = random.nextInt(10);
                        //if it's a valid move , shoot there
                        if(opponentBoard.getBoardCells()[x][y].isEmpty()){
                            try {
                                client.sendMessage(new Message(opponentUsername, Message.SHOOT, x, y));
                                // after shooting it's their turn
                                currentPlayer = OPPONENT;
                                onPlayerChanged();
                            } catch (IOException e) {
                                e.printStackTrace();
                                showError("An error occurred, check your network connection.");
                            }
                            shot = true;
                        }
                    }
                }
            }
        });

        clockPanel.add(clock);
        topPanel.add(clockPanel , BorderLayout.CENTER);


        //this label is used to show the game state to the user
        final JPanel statusPanel = new JPanel();
        //statusPanel.setBackground(Color.white);
        statusPanel.setOpaque(false);
        statusLabel = new JLabel("← To begin , place your ships on the left board");
        statusPanel.add(statusLabel);
        statusLabel.setFont(font.deriveFont(11f));
        statusLabel.setForeground(Color.white);
        topPanel.add(statusPanel,BorderLayout.SOUTH);

        frameContent.add(topPanel, BorderLayout.NORTH);


        Border boardBoard = new EmptyBorder(new Insets(0,36,0,36));
		// create an inner panel to hold the two boards
		JPanel innerPanel = new JPanel(new GridLayout(0, 2, 36, 0));
        innerPanel.setBorder(boardBoard);
		frameContent.add(innerPanel, BorderLayout.CENTER);
        innerPanel.setOpaque(false);


		// set up the local board
		localBoard
				.addShipPlacementListener(new BattleBoardLocal.ShipPlacementListener() {
					@Override
					public void onFinished() {
						try {
							// tell the other user you're ready
							client.sendMessage(new Message(opponentUsername,
									Message.READY_TO_PLAY));
							// if the other opponent hasn't finished yet, you go
							// first
							if (currentPlayer == 0) {
								currentPlayer = ME;
                        statusLabel.setText("Your opponent is not ready yet.");
							}
                    else onPlayerChanged();
							// we're ready
							playerReady = true;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
        //localBoard.setBackground(Color.white);
        localBoard.setOpaque(false);
		innerPanel.add(localBoard);

		// set up the opponent board
		opponentBoard.addShotListener(new BattleBoardOpponent.ShotListener() {
			@Override
			public void onShotFired(int x, int y) {
				// both sides must be ready
				if (!playerReady || !opponentReady) {

                    showError(!playerReady?
                            "You haven't placed your ships yet." :
                            "Your opponent hasn't placed their ships");
					return;
				}

                if(opponentBoard.getBoardCells()[x][y].object.getIcon() !=null){
                    showError("You already shot there.");
                    return;
                }

				// you must be the current player to shoot
				if (currentPlayer == ME) {
					try {
						client.sendMessage(new Message(opponentUsername,
								Message.SHOOT, x, y));
						// after shooting it's their turn
						currentPlayer = OPPONENT;
						onPlayerChanged();
					} catch (IOException e) {
						e.printStackTrace();
						showError("An error occurred, check your network connection.");
					}
				} else {
					showError("It's not your turn right now");
				}
			}
		});
        opponentBoard.setOpaque(false);
		innerPanel.add(opponentBoard);

        //to sort of center the chatting panel
        Border chattingAreaPadding = new EmptyBorder(new Insets(0,36+18,0,36+18));
        //the chattingContainer is transparent and has a padding
        //the chattingArea is white and is inside the chattingContainer
        JPanel chattingContainer = new JPanel(new BorderLayout());
		// set up the chat area
		JPanel chattingArea = new JPanel(new BorderLayout());
        chattingArea.setBorder(new LineBorder(Color.gray , 1));
        chattingContainer.setBorder(chattingAreaPadding);
        chattingArea.setBackground(Color.white);
        chattingContainer.setOpaque(false);
		chattingArea.setSize(new Dimension(0, 70));
		chattingArea.setPreferredSize(new Dimension(0, 200));
        chattingContainer.setSize(new Dimension(0, 70));
        chattingContainer.setPreferredSize(new Dimension(0, 200));
        chattingContainer.add(chattingArea , BorderLayout.CENTER);
		frameContent.add(chattingContainer, BorderLayout.SOUTH);


        Border standardPadding = new EmptyBorder(new Insets(10,10,10,10));

		// messaging output
		messagesPane = new JTextPane();
		messagesPane.setContentType("text/html");
		messagesPane.setEditable(false);
        messagesPane.setBorder(standardPadding);
        messagesPane.setFont(font.deriveFont(13f));
		chattingArea.add(new JScrollPane(messagesPane), BorderLayout.CENTER);

        chatLabel = new JLabel("Chat");
        chatLabel.setForeground(Color.darkGray);
        chatLabel.setBorder(standardPadding);
        chatLabel.setFont(font.deriveFont(13f));
        chattingArea.add(chatLabel , BorderLayout.NORTH);

		// messaging input
		final JTextField messageInput = new JTextField();
        //not sure if this is a good idea:
        //messageInput.setFont(font.deriveFont(13f));
        messageInput.setBorder(standardPadding);
		chattingArea.add(messageInput, BorderLayout.SOUTH);
		messageInput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// send message when the user presses enter
				Message toSend = new Message(opponentUsername,
						Message.CHAT_MESSAGE, messageInput.getText().trim());
				try {
					client.sendMessage(toSend);
					appendToLog("\n<strong>" + playerUsername + ":</strong> "
							+ messageInput.getText());
					messageInput.setText("");
				} catch (IOException ex) {
					appendToLog("\nFailed to send message");
					ex.printStackTrace();
				}

			}
		});

		setVisible(true);
        getLayeredPane().repaint();
	}

	@Override
	public void onReceiveMessage(Message msg) {
		if (msg == null)
			return;

		switch (msg.getType()) {
		case Message.SET_OPPONENT:
			if (msg.getMessage() == null)
				return;
			opponentUsername = msg.getMessage();
			setTitle(playerUsername + " (you) vs. " + opponentUsername);
                //chatLabel.setText("Chat with " + opponentUsername);
			break;

		case Message.CHAT_MESSAGE:
			if (msg.getMessage() == null)
				return;
			appendToLog("\n<strong>" + opponentUsername + ":</strong> "
					+ msg.getMessage());
			break;

		case Message.OPPONENT_DISCONNECTED:
			appendToLog("\n<strong>-- Your opponent disconnected! --");
			// TODO: automatically win the game here
			break;

		case Message.READY_TO_PLAY:
			opponentReady = true;
			// if you haven't finished yet, they will go first
			if (currentPlayer == 0) {
				currentPlayer = OPPONENT;
				onPlayerChanged();
				appendToLog("\n<strong>" + opponentUsername + " has finished placing ships and is ready to play</strong>");
			}
			break;

		case Message.SHOOT: {

			// you've been shot at, so it's your turn
			currentPlayer = ME;
			onPlayerChanged();

			// find the tile they shop
			BattleAnimationPanel shotAt = localBoard.getBoardCells()[msg.getX()][msg
					.getY()];

			// notify the opponent whether it was a hit or a miss
			if (shotAt.isEmpty()) {
				shotAt.setAsMissPin();
				try {
					client.sendMessage(new Message(opponentUsername,
							Message.MISS, msg.getX(), msg.getY()));
				} catch (IOException e) {
                        showError("An error occurred, check your network connection.");
					e.printStackTrace();
				}
			} else {
				shotAt.explode();
				try {
					client.sendMessage(new Message(opponentUsername,
							Message.HIT, msg.getX(), msg.getY()));
				} catch (IOException e) {
                        showError("An error occurred, check your network connection.");
					e.printStackTrace();
				}
			}
                //clock.start();
			break;
		}

		// you hit the opponent
		case Message.HIT:
			BattleAnimationPanel hitAt = opponentBoard.getBoardCells()[msg
					.getX()][msg.getY()];
			hitAt.setAsHitPin();
			opponentBoard.incDestroyedShipsPieces();

			// A simple test that checks after each players
			// hits if that is the last remaining ship.
			if (opponentBoard.getDestroyedShipPieces() == opponentBoard
					.getTotalNoOfShipPiecesToDestroy()) {
				try {
					onWin();
					// send a message to the opponent that they have lost.
					client.sendMessage(new Message(opponentUsername,Message.PLAYER_LOSE));
				} catch (IOException e) {
					// TODO handle error
					e.printStackTrace();
				}
			}
				break;

			// you missed the opponent
		case Message.MISS:
			BattleAnimationPanel missAt = opponentBoard.getBoardCells()[msg
					.getX()][msg.getY()];
			missAt.setAsMissPin();
			break;
		case Message.PLAYER_LOSE:
			onLose();
		}
	}

	private void onLose() {
		appendToLog("You have lost.");
		// TODO: do something when the player loses.
	}

	private void onWin() {
		appendToLog("You have won.");
		// TODO : do something when the player wins.
	}

	private void appendToLog(String s) {
		messages.add(s);

		// this code appends a line as html
		HTMLDocument doc = (HTMLDocument) messagesPane.getDocument();
		HTMLEditorKit editorKit = (HTMLEditorKit) messagesPane.getEditorKit();
		try {
			editorKit.insertHTML(doc, doc.getLength(), s, 0, 0, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void onPlayerChanged() {

		if (currentPlayer == ME) {
            clock.start();
		} else {
            clock.stop();
		}

        statusLabel.setForeground(Color.white);
        statusLabel.setText(currentPlayer == ME ?
                "It's your turn.Press a square on the right board to shoot →":
                "It's the opponent's turn now.");
	}

    private void showError(String msg){
        statusLabel.setForeground(Color.red);
        statusLabel.setText(msg);
    }

    public static void main(String[] arr){
        new BattleClientGui().setVisible(true);
    }
}
