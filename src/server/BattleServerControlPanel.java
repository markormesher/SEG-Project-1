package server;

import client.BattleClientGui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class BattleServerControlPanel extends JFrame {

	public static void main(String[] arr) {
		// make the UI fit the OS defaults
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception useDefault) {
					// use default layout
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

		//server runs on a thread
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

		// list of connected users
		final DefaultListModel<String> users = new DefaultListModel<String>();
		JList<String> list = new JList<String>(users);
		serverUI.setLayout(new BorderLayout());
		serverUI.add(list, BorderLayout.CENTER);

		// when a new connection is made and is assigned a name, add the username to the list
		server.addClientConnectedListener(new ClientConnectedListener() {
			@Override
			public void onClientConnected(String name) {
				users.addElement(name);
			}
		});

		// when a connection dies, remove them from the list
		server.addClientDisconnectedListener(new ClientDisconnectedListener() {
			@Override
			public void onClientDisconnected(String name) {
				users.removeElement(name);
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
