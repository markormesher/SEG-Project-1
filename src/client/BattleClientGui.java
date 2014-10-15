package client;

import client.ui_components.BattleAnimationPanel;
import client.ui_components.BattleBoardLocal;
import client.ui_components.BattleBoardOpponent;
import client.ui_components.CountdownClock;
import global.Message;
import global.Settings;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class BattleClientGui extends JFrame implements BattleClientGuiInterface {

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
	ArrayList<String> messages = new ArrayList<String>();

	// messaging output
	private JTextPane messagesPane;

	// the two boards
	private BattleBoardLocal localBoard = new BattleBoardLocal();
	private BattleBoardOpponent opponentBoard = new BattleBoardOpponent();

	public BattleClientGui() {
		// basic setup of frame
		setSize(750, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

        /*CountdownClock clock = new CountdownClock(30);
		clock.setSize(300,300);
        clock.setOpaque(false);
        this.add(clock);*/

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

		// create an inner panel to hold the two boards
		JPanel innerPanel = new JPanel(new GridLayout(0, 2, 10, 10));
		add(innerPanel, BorderLayout.CENTER);

		// set up the local board
		localBoard.addShipPlacementListener(new BattleBoardLocal.ShipPlacementListener() {
			@Override
			public void onFinished() {
				try {
					// tell the other user you're ready
					client.sendMessage(new Message(opponentUsername, Message.READY_TO_PLAY));
					// if the other opponent hasn't finished yet, you go first
					if (currentPlayer == 0) {
						currentPlayer = ME;
					}
					// we're ready
					playerReady = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		innerPanel.add(localBoard);

		//TODO finish this clock thing
		JPanel clockContainer = new JPanel(new FlowLayout());
		CountdownClock clock = new CountdownClock(60);
		clock.setMaximumSize(new Dimension(50, 50));
		clock.setPreferredSize(new Dimension(50, 50));
		clockContainer.add(clock);
		//inner.add(clockContainer);

		// set up the opponent board
		opponentBoard.addShotListener(new BattleBoardOpponent.ShotListener() {
			@Override
			public void onShotFired(int x, int y) {
				// both sides must be ready
				if (!playerReady || !opponentReady) {
					// TODO: error message: both sides are not ready yet
					return;
				}

				// you must be the current player to shoot
				if (currentPlayer == ME) {
					try {
						client.sendMessage(new Message(opponentUsername, Message.SHOOT, x, y));
						// after shooting it's their turn
						currentPlayer = OPPONENT;
						onPlayerChanged();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					// TODO: error message: "not your turn"
				}
			}
		});
		innerPanel.add(opponentBoard);

		// set up the chat area
		JPanel chattingArea = new JPanel(new BorderLayout());
		chattingArea.setSize(new Dimension(0, 70));
		chattingArea.setPreferredSize(new Dimension(0, 150));
		add(chattingArea, BorderLayout.SOUTH);

		// messaging output
		messagesPane = new JTextPane();
		messagesPane.setContentType("text/html");
		messagesPane.setEditable(false);
		chattingArea.add(new JScrollPane(messagesPane), BorderLayout.CENTER);

		// messaging input
		final JTextField messageInput = new JTextField();
		chattingArea.add(messageInput, BorderLayout.SOUTH);
		messageInput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// send message when the user presses enter
				Message toSend = new Message(opponentUsername, Message.CHAT_MESSAGE, messageInput.getText().trim());
				try {
					client.sendMessage(toSend);
					appendToLog("\n<strong>" + playerUsername + ":</strong> " + messageInput.getText());
					messageInput.setText("");
				} catch (IOException ex) {
					appendToLog("\nFailed to send message");
					ex.printStackTrace();
				}

			}
		});

		setVisible(true);
	}

	@Override
	public void onReceiveMessage(Message msg) {
		if (msg == null) return;

		switch (msg.getType()) {
			case Message.SET_OPPONENT:
				if (msg.getMessage() == null) return;
				opponentUsername = msg.getMessage();
				setTitle(playerUsername + " (you) vs. " + opponentUsername);
				break;

			case Message.CHAT_MESSAGE:
				if (msg.getMessage() == null) return;
				appendToLog("\n<strong>" + opponentUsername + ":</strong> " + msg.getMessage());
				break;

			case Message.OPPONENT_DISCONNECTED:
				appendToLog("\n<strong>-- Your opponent disconnected! --");
				// TODO: automatically win the game here
				break;

			case Message.READY_TO_PLAY:
				opponentReady = true;
				// if you haven't finished yet, they will go first
				if (currentPlayer == 0) currentPlayer = OPPONENT;
				appendToLog("\n<strong>" + opponentUsername + " has finished placing ships and is ready to play</strong>");
				break;

			case Message.SHOOT: {
				// you've been shot at, so it's your turn
				currentPlayer = ME;
				onPlayerChanged();

				// find the tile they shop
				BattleAnimationPanel shotAt = localBoard.getBoardCells()[msg.getX()][msg.getY()];

				// notify the opponent whether it was a hit or a miss
				if (shotAt.isEmpty()) {
					shotAt.setAsMissPin();
					try {
						client.sendMessage(new Message(opponentUsername, Message.MISS, msg.getX(), msg.getY()));
					} catch (IOException e) {
						// TODO: handle error
						e.printStackTrace();
					}
				} else {
					shotAt.setAsHitPin();
					try {
						client.sendMessage(new Message(opponentUsername, Message.HIT, msg.getX(), msg.getY()));
					} catch (IOException e) {
						// TODO: handle error
						e.printStackTrace();
					}
				}
				break;
			}

			// you hit the opponent
			case Message.HIT:
				BattleAnimationPanel hitAt = opponentBoard.getBoardCells()[msg.getX()][msg.getY()];
				hitAt.setAsHitPin();
				break;

			// you missed the opponent
			case Message.MISS:
				BattleAnimationPanel missAt = opponentBoard.getBoardCells()[msg.getX()][msg.getY()];
				missAt.setAsMissPin();
				break;
		}
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
		// TODO: update status message somewhere when the player changes
		if (currentPlayer == ME) {
			setTitle(playerUsername + " (you) vs. " + opponentUsername + " | YOUR MOVE");
		} else {
			setTitle(playerUsername + " (you) vs. " + opponentUsername + " | THEIR MOVE");
		}
	}
}