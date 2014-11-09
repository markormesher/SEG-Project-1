package server;

import client.BattleClientGui;
import global.Settings;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Date;

public class BattleServerControlPanel extends JFrame {

	public static void main(String[] arr) {

		// make the UI fit the OS defaults
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception useDefault) {
					// at least we tried - use default layout
				}
				initUi();
			}
		});
	}

	public static void initUi() {
		// create a basic UI
		final BattleServerControlPanel serverUI = new BattleServerControlPanel();
		serverUI.setSize(300, 600);
		serverUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		serverUI.setTitle("Battle Server Control Panel");

		// server runs on a thread
		final BattleServer server = new BattleServer();
		(new Thread() {
			public void run() {
				try {
					server.startServer();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(serverUI, "Starting server failed.");
					e.printStackTrace();
				}
			}
		}).start();

		// create list cell renderer to disable selection
		DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index,
														  boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, false, false);

				// set different font color for connection, disconnection and errors
				String msg = (String) value;
				if (msg.contains("New client")) {
					this.setForeground(Color.BLUE);
				} else if (msg.contains("disconnected")) {
					this.setForeground(Color.DARK_GRAY);
				} else {
					this.setForeground(Color.RED);
				}

				return this;
			}
		};

		// list of server messages
		final DefaultListModel<String> serverMessages = new DefaultListModel<String>();
		JList<String> list = new JList<String>(serverMessages);
		list.setCellRenderer(listCellRenderer);
		serverUI.setLayout(new BorderLayout());
		serverUI.add(new JScrollPane(list), BorderLayout.CENTER);

		// when a new connection is made and is assigned a name, add the username to the list
		server.addClientConnectedListener(new ClientConnectedListener() {
			@Override
			public void onClientConnected(String name) {
				Date date = new Date();
				serverMessages.addElement(Settings.SERVER_DATE_FORMAT.format(date) + " New client connected: " + name);
			}
		});

		// when a connection dies, add notification to list
		server.addClientDisconnectedListener(new ClientDisconnectedListener() {
			@Override
			public void onClientDisconnected(String name) {
				Date date = new Date();
				serverMessages.addElement(Settings.SERVER_DATE_FORMAT.format(date) + " Client disconnected: " + name);
			}
		});

		// add all other messages received by server to list
		server.addServerMessageListener(new ServerMessageListener() {
			@Override
			public void onServerMessageReceived(String serverMessage) {
				Date date = new Date();
				serverMessages.addElement(Settings.SERVER_DATE_FORMAT.format(date) + " " + serverMessage);
			}
		});

		// spawn two clients to demo with
		BattleClientGui battleClientGui1 = new BattleClientGui();
		battleClientGui1.setLocation(serverUI.getLocation().x + serverUI.getWidth() + 10, serverUI.getLocation().y);
		battleClientGui1.setVisible(true);
		BattleClientGui battleClientGui2 = new BattleClientGui();
		battleClientGui2.setLocation(serverUI.getLocation().x + serverUI.getWidth() + battleClientGui1.getWidth() + 20, serverUI.getLocation().y);
		battleClientGui2.setVisible(true);

		// set this window as visible
		serverUI.setVisible(true);
	}

}