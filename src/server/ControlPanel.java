package server;

import client.ui.ClientUI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ControlPanel extends JFrame{
    public static void main(String[] arr) {
        //this makes the UI fit the OS
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                } catch (Exception useDefault) {}
                initUI();
            }
        });



    }

    public static void initUI(){
        final ControlPanel serverUI = new ControlPanel();
        serverUI.setSize(300, 600);
        serverUI.setVisible(true);
        serverUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        serverUI.setTitle("Control Panel");
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

        //list of connected users
        final DefaultListModel users = new DefaultListModel();
        JList list = new JList(users);

        serverUI.setLayout(new BorderLayout());
        serverUI.add(list , BorderLayout.CENTER);

        //when a new connection is made and is assigned a name , add the username to the list
        server.addNewConnectionListener(new NewConnectionListener() {
            @Override
            public void onNewConnection(String name) {
                users.addElement(name);
            }
        });

        //for demo purposes start 2 clients
        ClientUI clientUI = new ClientUI();
        clientUI.setLocation(serverUI.getLocation().x + serverUI.getWidth()+10 ,serverUI.getLocation().y );
        clientUI.setVisible(true);
        ClientUI clientUI2 = new ClientUI();
        clientUI2.setLocation(serverUI.getLocation().x + serverUI.getWidth() + clientUI.getWidth()+20 ,serverUI.getLocation().y );

        clientUI2.setVisible(true);
    }
}
