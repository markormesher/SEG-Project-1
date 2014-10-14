package client;

import global.Message;
import global.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class BattleClientGui implements BattleClientGuiInterface {

	public static void main(String[] args) {
		// create a new gui and start a client
		BattleClientGui battleClientGui = new BattleClientGui();
		try {
			battleClientGui.startClient();
		} catch (IOException e) {
			System.out.println("# Failed to create client");
		}

		// this really basic implementation loops forever and allows you to send
		// a message to another client, using the format "username:message"
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String msg = scanner.nextLine();
			String[] msgParts = msg.split(":");
			if (msgParts.length == 2) {
				Message toSend = new Message(msgParts[0], Message.MESSAGE, msgParts[1].trim());
				try {
					battleClientGui.sendMessage(toSend);
				} catch (IOException e) {
					System.out.println("# Failed to send message");
					e.printStackTrace();
				}
			}
		}
	}

	// settings for this game
	private BattleClient client;
	private String opponentUsername;

	// start a client and connect to the server
	public void startClient() throws IOException {
		// get connection details
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("# Welcome to Battleship v0.1");
		System.out.println("# ==========================");
		System.out.println("# ");
		System.out.println("# We need some information to connect you to the server...");

		// get host
		String host;
		System.out.print("# Enter server name [" + Settings.HOST_NAME + "]: ");
		host = br.readLine();
		if (host.equals("")) host = Settings.HOST_NAME;
		System.out.println("#");

		// get port
		int port;
		System.out.print("# Enter port number [" + Settings.PORT_NUMBER + "]: ");
		try {
			String tempPort = br.readLine();
			if (tempPort.equals("")) {
				port = Settings.PORT_NUMBER;
			} else {
				port = Integer.parseInt(tempPort);
			}
			System.out.println("#");
		} catch (NumberFormatException e) {
			System.out.println("# You didn't enter a valid port number");
			System.out.println("# Goodbye!");
			return;
		}

		// get client name
		String username;
		System.out.print("# Enter a unique user name: ");
		username = br.readLine();
		if (username.equals("")) {
			System.out.println("# You didn't enter a valid user name");
			System.out.println("# Goodbye!");
			return;
		}
		System.out.println("#");

		// try to establish a connection to the server
		client = new BattleClient(host, port, username, this);
		try {
			System.out.println("# Connecting to " + host + ":" + port + " as " + username);
			client.connect();
			System.out.println("# Connection established");
			System.out.println("# Waiting for opponent...");
		} catch (IOException e) {
			System.out.println("# Cannot establish connection - is the server running?");
		}
	}

	// this method will be called every time a message is sent to this client
	@Override
	public void onReceiveMessage(Message msg) {
		switch (msg.getType()) {
			case Message.SET_OPPONENT:
				opponentUsername = msg.getMessage();
				System.out.println("# You are playing against " + opponentUsername);
				break;

			case Message.SERVER_GONE:
				System.out.println("# The connection to the server has been lost");
				break;

			default:
				if (msg.getMessage() != null) {
					System.out.println("# " + msg.getMessage());
				}
				break;
		}
	}

	// this method can be used to send a message back to the server
	public void sendMessage(Message msg) throws IOException {
		client.sendMessage(msg);
	}
}
