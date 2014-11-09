package client;

import global.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BattleClient {

	// connection details
	private String host;
	private int port;

	// front end
	public BattleClientGuiInterface gui;

	// server streams
	private Socket serverSocket;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;

	public BattleClient(String host, int port, BattleClientGuiInterface gui) {
		this.host = host;
		this.port = port;
		this.gui = gui;
	}

	public void connect() throws IOException {
		// connect to server
		serverSocket = new Socket(host, port);

		// get object streams
		inputStream = new ObjectInputStream(serverSocket.getInputStream());
		outputStream = new ObjectOutputStream(serverSocket.getOutputStream());

		// listen for incoming messages from the server
		new ListenFromServer().start();
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
					if (gui != null) gui.onReceiveMessage(msg);
				} catch (IOException e) {
					Message serverGone = new Message(null, Message.SERVER_GONE);
					if (gui != null) gui.onReceiveMessage(serverGone);
					break;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}

}
