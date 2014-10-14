package server;

import global.ClientPair;
import global.Message;
import global.Settings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BattleServer {

	// start a server
	public static void main(String[] args) {
		BattleServer server = new BattleServer();
		try {
			server.startServer();
		} catch (IOException e) {
			System.out.println("Failed to start server");
			e.printStackTrace();
		}
	}

	// waiting to join a game
	private BattleClientThread waitingClient = null;

	// list of connected clients
	private int connectedPlayerCounter = 0;
	private ArrayList<BattleClientThread> connectedClients = new ArrayList<BattleClientThread>();
	private ArrayList<ClientPair> activePairs = new ArrayList<ClientPair>();

	// start server
	public void startServer() throws IOException {
		System.out.println("Starting server on localhost:" + Settings.PORT_NUMBER);
		ServerSocket serverSocket = new ServerSocket(Settings.PORT_NUMBER);

		// loop forever, collecting client connections
		Socket socket;
		while (true) {
			// wait for a connection to arrive (blocks this thread)
			System.out.println("Waiting for a client to connect...");
			socket = serverSocket.accept();

			// create a new client thread
			System.out.println("New client connected");
			System.out.println("");
			BattleClientThread thread = new BattleClientThread(socket);

			// store the thread
			connectedClients.add(thread);

			// set up a game, or save them in the queue
			if (waitingClient == null) {
				// wait in the queue
				waitingClient = thread;
			} else {
				// create a new game
				ClientPair pair = new ClientPair();
				pair.clientA = waitingClient;
				pair.clientB = thread;

				// store the new game
				activePairs.add(pair);
				waitingClient = null;
			}

			// start the thread
			thread.start();
		}
	}

	protected synchronized void checkBothUsernameSet(int id) {
		// find pair where one of the IDs match
		ClientPair pair = null;
		for (ClientPair p : activePairs) {
			if (p.clientA.id == id || p.clientB.id == id) {
				pair = p;
			}
		}

		// are both username set?
		if (pair != null) {
			if (pair.clientA.username != null && pair.clientB.username != null) {
				// tell each client their opponents username
				Message sendToA = new Message(pair.clientA.username, Message.SET_OPPONENT, pair.clientB.username);
				Message sendToB = new Message(pair.clientB.username, Message.SET_OPPONENT, pair.clientA.username);
				try {
					pair.clientA.sendMessage(sendToA);
					pair.clientB.sendMessage(sendToB);
				} catch (IOException e) {
					System.out.println("Failed to send opponent username to " + pair.clientA.username + " and/or " + pair.clientB.username);
				}
			}
		}
	}

	protected synchronized void removeClientThread(int id) {
		// scan connected clients and remove this one
		for (BattleClientThread c : connectedClients) {
			if (c.id == id) {
				connectedClients.remove(c);
			}
		}

		// was this one waiting?
		if (waitingClient != null && waitingClient.id == id) {
			waitingClient = null;
			return;
		}

		// scan active games and remove this one, and notify the opponent
		BattleClientThread opponent = null;
		for (ClientPair p : activePairs) {
			// find the disconnected client's opponent
			if (p.clientA.id == id) {
				opponent = p.clientB;
			}
			if (p.clientB.id == id) {
				opponent = p.clientA;
			}

			// notify the other client
			if (opponent != null) {
				Message sendToOp = new Message(opponent.username, Message.OPPONENT_DISCONNECTED);
				try {
					opponent.sendMessage(sendToOp);
				} catch (IOException e) {
					System.out.println("Failed to notify " + opponent.username + " that their opponent disconnected");
				}

				// remove this pair
				activePairs.remove(p);

				// don't waste time
				break;
			}
		}
	}

	// a new BattleClientThread is created for each client that connects
	public class BattleClientThread extends Thread {

		// store this client's identifiers
		public int id; // created straight away
		public String username = ""; // set shortly after creation

		// server streams
		private Socket serverSocket;
		private ObjectInputStream inputStream;
		private ObjectOutputStream outputStream;

		public BattleClientThread(Socket serverSocket) throws IOException {
			this.serverSocket = serverSocket;

			// create IO streams
			// IMPORTANT: create output stream first
			outputStream = new ObjectOutputStream(serverSocket.getOutputStream());
			inputStream = new ObjectInputStream(serverSocket.getInputStream());

			// get a new ID
			id = connectedPlayerCounter++;
		}

		// close all sockets and streams
		public void close() throws IOException {
			if (inputStream != null) inputStream.close();
			if (outputStream != null) outputStream.close();
			if (serverSocket != null) serverSocket.close();
		}

		// send a message out to the client linked to this client thread
		public void sendMessage(Message msg) throws IOException {
			this.outputStream.writeObject(msg);
		}

		// listen for incoming messages from the client and decide what to do with them
		@Override
		public void run() {
			while (true) {
				// try to read from stream
				Message msg;
				try {
					msg = (Message) inputStream.readObject();
				} catch (ClassNotFoundException e) {
					System.out.println("Invalid message object sent");
					break;
				} catch (IOException e) {
					System.out.println("Client disconnected: " + username);
					break;
				}

				// setting username or sending message?
				if (msg.getType() == Message.SET_USERNAME) {
					// set username of this thread
					username = msg.getMessage();
					checkBothUsernameSet(id);
                    for(NewConnectionListener listener:listeners)listener.onNewConnection(username);
				} else {
					// find recipient by username and send the message to them
					for (BattleClientThread c : connectedClients) {
						if (c.username.equalsIgnoreCase(msg.getRecipient())) {
							try {
								c.sendMessage(msg);
							} catch (IOException e) {
								System.out.print("Failed to send message to " + msg.getRecipient());
							}
						}
					}
				}
			}

			// finished the loop, so remove this client from the array
			removeClientThread(id);

			// shut everything down
			try {
				close();
			} catch (IOException e) {
				System.out.println("Failed to close client thread");
			}
		}
	}

    ArrayList<NewConnectionListener> listeners = new ArrayList<NewConnectionListener>();

    public void addNewConnectionListener(NewConnectionListener listener){
        listeners.add(listener);
    }

}
