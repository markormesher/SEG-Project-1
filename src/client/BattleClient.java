package client;

import global.Message;

import java.io.*;
import java.net.Socket;

public class BattleClient {

	// connection details
	private String host;
	private int port;
	private String username;

	// front end
	private BattleClientGuiInterface gui;

	// server streams
	private Socket serverSocket;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;

	public BattleClient(String host, int port, String username, BattleClientGuiInterface gui) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.gui = gui;
	}

	public void connect() throws IOException {
		// connect to server
		System.out.println("# Connecting to " + host + ":" + port + " as " + username);
		serverSocket = new Socket(host, port);
		System.out.println("# Connection Established");

		// get object streams
		inputStream = new ObjectInputStream(serverSocket.getInputStream());
		outputStream = new ObjectOutputStream(serverSocket.getOutputStream());

		// listen for incoming messages from the server
		new ListenFromServer().start();

		// send username to server
		Message setUsername = new Message(null, Message.SET_USERNAME, username);
		outputStream.writeObject(setUsername);
	}

	public void disconnect() throws IOException {
		if (inputStream != null) inputStream.close();
		if (outputStream != null) outputStream.close();
		if (serverSocket != null) serverSocket.close();
	}

	public void sendMessage(Message msg) throws IOException {
		outputStream.writeObject(msg);
	}

	public class ListenFromServer extends Thread {

		@Override
		public void run() {
			while (true) {
				try {
					Message msg = (Message) inputStream.readObject();
					if (msg.getMessage() != null) {
						gui.onReceiveMessage(msg);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}