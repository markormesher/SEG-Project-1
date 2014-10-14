package server;

import global.Message;
import global.Settings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.SortedSet;

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

	// list of connected clients
	private ArrayList<BattleClientThread> connectedClients = new ArrayList<BattleClientThread>();

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

			// store the connection
			System.out.println("New client connected");
			System.out.println("");
			BattleClientThread thread = new BattleClientThread(socket);
			connectedClients.add(thread);
			thread.start();
		}
	}

	protected synchronized void removeClientThread(String username) {
		// scan connected clients and remove this one
		for (BattleClientThread c : connectedClients) {
			if (c.username != null && username != null && c.username.equals(username)) {
				connectedClients.remove(c);
			}
		}
	}

	// a new BattleClientThread is created for each client that connects
	public class BattleClientThread extends Thread {

		// store client's username
		public String username = "";

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
				} else {
					// find recipient by username and send the message to them
					for (BattleClientThread c : connectedClients) {
						if (c.username.equalsIgnoreCase(msg.getRecipient())) {
							try {
								c.sendMessage(msg);
							} catch (IOException e) {
								System.out.print("Failed to send message to " + msg.getRecipient());
								e.printStackTrace();
							}
						}
					}
				}
			}

			// finished the loop, so remove this client from the array
			removeClientThread(username);

			// shut everything down
			try {
				close();
			} catch (IOException e) {
				System.out.println("Failed to close client thread");
				e.printStackTrace();
			}
		}
	}

}