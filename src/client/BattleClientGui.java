package client;

import client.ui_components.*;
import global.Message;
import global.Settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

// TODO: add listener for messages from the server (add them to the chat console?)

public class BattleClientGui extends JFrame implements BattleClientGuiInterface {

	// the 8-bit font
	public Font font;

    Result result;
    Result opponentResult;

	// used for random moves when the clock runs out
	private Random random = new Random();

	// the client handles all communication with server
	private BattleClient client;

	// user names
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
	private JLabel chatLabel;
	private JTextPane messagesPane;

    // messaging input
    private JTextField messageInput = new JTextField();

    // emoticons button
    private JButton emoticonsButton;

    // emoticon frame to pop up when emoticons button is clicked
    private EmoticonsFrame emoticonsFrame;

	// the label that displays the user the current status
	private JLabel statusLabel;

	// the countdown clock
	private CountdownClock clock;
    private int consecutiveTimeouts = 0;

	// the two boards
	private BattleBoardLocal localBoard = new BattleBoardLocal();
	private BattleBoardOpponent opponentBoard = new BattleBoardOpponent();

	// the background tile
	private BufferedImage backgroundTile;

	public BattleClientGui() {
		// loading the font from the ttf file
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/PressStart2P.ttf"));
			font = font.deriveFont(25f);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        // basic setup of frame
        setSize(Settings.GRID_SIZE * Settings.IMAGE_CELL_SIZE * 2 + Settings.IMAGE_CELL_SIZE * 3, Settings.IMAGE_CELL_SIZE * 24);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //This layer is drawn on top of the game and is used to login/signup
        final AuthLayer authLayer = new AuthLayer(getWidth(),getHeight());
        getLayeredPane().add(authLayer, new Integer(10));

        //when the user logs in
        authLayer.addAuthListener(new AuthLayer.AuthAdapter() {
            @Override
            public void onLogin(BattleClient _client , String username) {
                playerUsername = username;
                result = new Result(playerUsername, 0, 0, 0, false);
                setTitle(playerUsername + " (you) vs. ???");

                chatLabel = new JLabel("Chat");

                client = _client;
                client.gui = BattleClientGui.this;
                /*// the client uses while so it must run on a separate thread
                (new Thread() {
                    public void run() {
                        client = new BattleClient(Settings.HOST_NAME, Settings.PORT_NUMBER, playerUsername, BattleClientGui.this);
                        try {
                            client.connect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();*/

                //we hide the auth layer to reveal the game
                authLayer.setVisible(false);
                initUI();
            }
        });

	}


    //the logic that lays out the UI is now in this method
    //is called after log in
    public void initUI(){

        // the background is beneath the actual UI
        JPanel backgroundImage = new JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                if (backgroundTile == null) {
                    try {
                        backgroundTile = ImageIO.read(this.getClass().getResource("/images/default/bg-plain.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // cover the surface with background tiles
                for (int x = 0; x < getWidth() / 32.0; x++) {
                    for (int y = 0; y < getHeight() / 32.0; y++) {
                        g.drawImage(backgroundTile, x * Settings.IMAGE_CELL_SIZE, y * Settings.IMAGE_CELL_SIZE, Settings.IMAGE_CELL_SIZE, Settings.IMAGE_CELL_SIZE, null);
                    }
                }
            }
        };
        backgroundImage.setSize(new Dimension(getWidth(), getHeight()));
        backgroundImage.setPreferredSize(new Dimension(getWidth(), getHeight()));
        backgroundImage.repaint();

        // this panel contains the actual ui and is over the background
        JPanel frameContent = new JPanel();
        frameContent.setLayout(new BorderLayout());
        frameContent.setOpaque(false);
        frameContent.setSize(new Dimension(getWidth(), getHeight() - 18));

        // collect player username and set it as title
        //playerUsername = askForUsername();

        /*while(true) {
            String errorMessage;

            if(playerUsername == null || playerUsername.equals("")) {
                errorMessage = "Invalid username, please try again.";
            }
            else if (BattleServer.usernameIsTaken(playerUsername)){
                errorMessage = "This username is already taken, please try again.";
            }
            else {
                break;
            }


            JOptionPane.showMessageDialog(this, errorMessage);
            playerUsername = askForUsername();
        }*/

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

		// the panel containing the battleship logo
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);
		JLabel title = new JLabel();
		title.setIcon(new ImageIcon(this.getClass().getResource("/images/logo.png")));
		topPanel.add(title, BorderLayout.NORTH);

        // the panel with the clock
        JPanel clockPanel = new JPanel(new FlowLayout());
        clockPanel.setOpaque(false);
        clockPanel.setPreferredSize(new Dimension(150, 138));
        clockPanel.setSize(new Dimension(150, 138));

        // set up the clock
        clock = new CountdownClock(Settings.MOVE_TIMEOUT);
        clock.setSize(100, 100);
        clock.setPreferredSize(new Dimension(100, 100));
        clock.setOpaque(false);

        // triggered when the timer hits 0
        clock.addTimeoutListener(new CountdownClock.TimeoutListener() {
            @Override
            public void onTimeout() {
                if (currentPlayer == ME) {
                    boolean shot = false;
                    while (!shot) {
                        // find random tile
                        int x = random.nextInt(10);
                        int y = random.nextInt(10);
                        // if it's a valid move, shoot there
                        if (opponentBoard.getBoardCells()[x][y].isEmpty()) {
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

        // add clock
        clockPanel.add(clock);
        topPanel.add(clockPanel, BorderLayout.CENTER);

        // this label is used to show the game state to the user
        JPanel statusPanel = new JPanel();
        statusPanel.setOpaque(false);
        statusLabel = new JLabel("â†� To begin, place your ships on the left board");
        statusLabel.setFont(font.deriveFont(11f));
        statusLabel.setForeground(Color.white);
        statusPanel.add(statusLabel);
        topPanel.add(statusPanel, BorderLayout.SOUTH);

        // add all of the top panel to the main layout
        frameContent.add(topPanel, BorderLayout.NORTH);

        // create an inner panel to hold the two boards
        Border boardBoarder = new EmptyBorder(new Insets(0, 36, 0, 36));
        JPanel innerPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        innerPanel.setBorder(boardBoarder);
        innerPanel.setOpaque(false);
        frameContent.add(innerPanel, BorderLayout.CENTER);

        // set up the local board
        localBoard.addShipPlacementListener(new BattleBoardLocal.FinishedPlacingShipsListener() {
            @Override
            public void onFinished() {
                try {
                    // tell the other user you're ready
                    client.sendMessage(new Message(opponentUsername, Message.READY_TO_PLAY));

                    // if the other opponent hasn't finished yet, you go first
                    if (currentPlayer == 0) {
                        currentPlayer = ME;
                        statusLabel.setText("Your opponent is not ready yet.");
                    } else {
                        onPlayerChanged();
                    }

                    // we're ready
                    playerReady = true;
                } catch (IOException e) {
                    showError("An error occurred, check your network connection.");
                    e.printStackTrace();
                }
            }
        });

        localBoard.setOpaque(false);
        innerPanel.add(localBoard);

        // set up the opponent board
        opponentBoard.addShotListener(new BattleBoardOpponent.ShotListener() {
            @Override
            public void onShotFired(int x, int y) {
                // both sides must be ready
                if (!playerReady || !opponentReady) {
                    showError(!playerReady ? "You haven't placed your ships yet." : "Your opponent hasn't placed their ships");
                    return;
                }

                // check that they're not re-shooting on the same spot
                if (opponentBoard.getBoardCells()[x][y].object.getIcon() != null) {
                    showError("You already shot there.");
                    return;
                }

                // you must be the current player to shoot
                if (currentPlayer == ME) {
                    try {
                        client.sendMessage(new Message(opponentUsername, Message.SHOOT, x, y));
                        consecutiveTimeouts = 0;

                        // after shooting it's their turn
                        currentPlayer = OPPONENT;
                        onPlayerChanged();
                    } catch (IOException e) {
                        showError("An error occurred, check your network connection.");
                        e.printStackTrace();
                    }
                } else {
                    showError("It's not your turn right now");
                }
            }
        });

        opponentBoard.setOpaque(false);
        innerPanel.add(opponentBoard);

        // to center the chatting panel
        Border chattingAreaPadding = new EmptyBorder(new Insets(0, 36 + 18, 0, 36 + 18));

        // the chattingContainer is transparent and has a padding
        JPanel chattingContainer = new JPanel(new BorderLayout());
        // the chattingArea is white and is inside the chattingContainer
        JPanel chattingArea = new JPanel(new BorderLayout());
        // the chatInputPanel contains text field and emoticons button and is inside the chattingArea
        JPanel chatInputPanel = new JPanel(new BorderLayout());

        // set up the chat area
        chatInputPanel.setBackground(Color.white);
        chattingArea.setBorder(new LineBorder(Color.gray, 1));
        chattingArea.setBackground(Color.white);
        chattingArea.setSize(new Dimension(0, 70));
        chattingArea.setPreferredSize(new Dimension(0, 200));
        chattingContainer.setBorder(chattingAreaPadding);
        chattingContainer.setOpaque(false);
        chattingContainer.setSize(new Dimension(0, 70));
        chattingContainer.setPreferredSize(new Dimension(0, 200));
        chattingArea.add(chatInputPanel, BorderLayout.SOUTH);
        chattingContainer.add(chattingArea, BorderLayout.CENTER);
        frameContent.add(chattingContainer, BorderLayout.SOUTH);

        // messaging output
        Border standardPadding = new EmptyBorder(new Insets(10, 10, 10, 10));
        messagesPane = new JTextPane();
        messagesPane.setContentType("text/html");
        messagesPane.setEditable(false);
        messagesPane.setBorder(standardPadding);
        messagesPane.setFont(font.deriveFont(13f));
        chattingArea.add(new JScrollPane(messagesPane), BorderLayout.CENTER);

        chatLabel.setForeground(Color.darkGray);
        chatLabel.setBorder(standardPadding);
        chatLabel.setFont(font.deriveFont(13f));
        chattingArea.add(chatLabel, BorderLayout.NORTH);

        // emoticons frame and button
        emoticonsButton = new JButton();
        emoticonsFrame = new EmoticonsFrame(messageInput);

        try {
            emoticonsButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/images/emoticons/grin.png"))));
        } catch (IOException e) {
            //TODO: handle exception
        }
        emoticonsButton.setSize(new Dimension(16,16));
        emoticonsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!emoticonsFrame.isVisible()) {
                    emoticonsFrame.setLocation((int) emoticonsButton.getLocationOnScreen().getX(), (int) emoticonsButton.getLocationOnScreen().getY() + -80);
                    emoticonsFrame.setVisible(true);
                }
                else {
                    emoticonsFrame.setVisible(false);
                }
            }
        });

        // messaging input layout and listener
        messageInput.setBorder(standardPadding);
        chatInputPanel.add(messageInput, BorderLayout.CENTER);
        chatInputPanel.add(emoticonsButton, BorderLayout.EAST);
        messageInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // send message when the user presses enter
                String messageText = messageInput.getText().trim();
                //if message contains emoticon shortcut, convert to image html link
                if(emoticonsFrame.containsEmoticons(messageText)) {
                    messageText = emoticonsFrame.convertTextToHTML(messageText);
                }
                Message toSend = new Message(opponentUsername, Message.CHAT_MESSAGE, messageText);
                try {
                    client.sendMessage(toSend);
                    appendToLog("\n<strong>" + playerUsername + ":</strong> " + messageText);
                    messageInput.setText("");
                } catch (IOException ex) {
                    appendToLog("\nFailed to send message");
                    ex.printStackTrace();
                }
            }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                // give an modal prompt asking if the user wants to exit
                int option =
                        JOptionPane.showOptionDialog(BattleClientGui.this, "Do you want to quit the game?", "Disconnect from game", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                if(option == JOptionPane.YES_OPTION) {
                    // send message to opponent that i have disconnected.
                    try {
                        client.sendMessage(new Message(opponentUsername, Message.OPPONENT_DISCONNECTED));
                    } catch (IOException e1) {
                        // TODO: handle error
                        e1.printStackTrace();
                    } finally {
                        BattleClientGui.this.dispose();
                    }
                }
            }

        });

        // add layers
        getLayeredPane().add(backgroundImage, new Integer(1));
        getLayeredPane().add(frameContent, new Integer(2));

        // showtime!
        setVisible(true);
        getLayeredPane().repaint();
    }
    private String askForUsername() {
        return JOptionPane.showInputDialog(this, "Enter a username");
    }

	@Override
	public void onReceiveMessage(Message msg) {
		if (msg == null)
			return;

		switch (msg.getType()) {
			case Message.SET_OPPONENT:
				if (msg.getMessage() == null) {
					return;
				}
				opponentUsername = msg.getMessage();
				setTitle(playerUsername + " (you) vs. " + opponentUsername);
				chatLabel.setText("Chat with " + opponentUsername);
                opponentResult = new Result(opponentUsername,0,0,0,false);
				break;

			case Message.CHAT_MESSAGE:
				if (msg.getMessage() == null) {
					return;
				}
				appendToLog("\n<strong>" + opponentUsername + ":</strong> " + msg.getMessage());
				break;

			case Message.OPPONENT_DISCONNECTED:
				appendToLog("\n<strong>-- Your opponent disconnected! --");
				onWin();
				break;

			case Message.READY_TO_PLAY:
				opponentReady = true;
				// if you haven't finished yet, they will go first
				if (currentPlayer == 0) {
					currentPlayer = OPPONENT;
				}
				appendToLog("\n<strong>" + opponentUsername + " has finished placing ships and is ready to play</strong>");
				break;

			case Message.SHOOT: {
				// you've been shot at, so it's your turn
				currentPlayer = ME;
				onPlayerChanged();

				// find the tile they shot
				BattleAnimationPanel shotAt = localBoard.getBoardCells()[msg.getX()][msg.getY()];

                opponentResult.totalShots++;
				// notify the opponent whether it was a hit or a miss
				if (shotAt.isEmpty()) {
                    opponentResult.misses++;
					shotAt.setAsMissPin();
					try {
						client.sendMessage(new Message(opponentUsername, Message.MISS, msg.getX(), msg.getY()));
					} catch (IOException e) {
						showError("An error occurred, check your network connection.");
						e.printStackTrace();
					}
				} else {
                    opponentResult.hits++;
					shotAt.explode();
					try {
						client.sendMessage(new Message(opponentUsername, Message.HIT, msg.getX(), msg.getY()));
					} catch (IOException e) {
						showError("An error occurred, check your network connection.");
						e.printStackTrace();
					}
				}
				break;
			}

			// you hit the opponent
			case Message.HIT:
                result.totalShots++;
                result.hits++;
				BattleAnimationPanel hitAt = opponentBoard.getBoardCells()[msg.getX()][msg.getY()];
				hitAt.setAsHitPin();
				opponentBoard.incDestroyedShipPieces();

				// check if this was the winning shot
				if (opponentBoard.getDestroyedShipPieces() == opponentBoard.getTotalShipPieces()) {
					try {
						onWin();
						// send a message to the opponent that they have lost.
						client.sendMessage(new Message(opponentUsername, Message.PLAYER_LOSE));
					} catch (IOException e) {
						showError("An error occurred, check your network connection.");
						e.printStackTrace();
					}
				}
				break;

			// you missed the opponent
			case Message.MISS:
                result.totalShots++;
                result.misses++;
				BattleAnimationPanel missAt = opponentBoard.getBoardCells()[msg.getX()][msg.getY()];
				missAt.setAsMissPin();
				break;

			// you've lost
			case Message.PLAYER_LOSE:
				onLose();
				break;
		}
	}

	private void onLose() {
        opponentResult.won=true;
        ResultsWindow resultsWindow = new ResultsWindow(result,opponentResult,BattleClientGui.this);
        resultsWindow.setVisible(true);
        gameHasEnded();
		appendToLog("You have lost.");
	}

	private void onWin() {
        result.won = true;
		ResultsWindow resultsWindow = new ResultsWindow(result,opponentResult,BattleClientGui.this);
        resultsWindow.setVisible(true);
        gameHasEnded();
		appendToLog("You have won.");
	}

    public void disconnect() {
        // send message to opponent that i have disconnected.
        try {
            client.sendMessage(new Message(opponentUsername, Message.OPPONENT_DISCONNECTED));
        } catch (IOException e1) {
            // TODO: handle error
            e1.printStackTrace();
        } finally {
            this.dispose();
        }
    }

    private void gameHasEnded() {
        opponentBoard.removeShotListener();
        clock.stop();
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setText(result.won ? "This game is now finished. You win !" : "This game is now finished. You lose");
    }

	private void appendToLog(String s) {
		messages.add(s);

		// this code appends a line as html
		HTMLDocument doc = (HTMLDocument) messagesPane.getDocument();
		HTMLEditorKit editorKit = (HTMLEditorKit) messagesPane.getEditorKit();
        //set html base path to resources folder
        URL resources = EmoticonsFrame.class.getResource("/images/emoticons/");
        doc.setBase(resources);
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

		// update the status label
		statusLabel.setForeground(Color.white);
		statusLabel.setText(currentPlayer == ME ? "It's your turn. Press a square on the right board to shoot â†’" : "It's the opponent's turn now.");
	}

	private void showError(String msg) {
		statusLabel.setForeground(Color.red);
		statusLabel.setText(msg);
	}

	public static void main(String[] arr) {
		new BattleClientGui();
	}
}
