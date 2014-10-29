package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import client.MainScreen.ScreenState;

public class HelpScreen extends Screen {
private JButton backButton = new JButton("Back");
	
	public HelpScreen(ScreenManager sm) {
		super(sm);
	
		setLayout(new BorderLayout());
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(backButton);
		backButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				screenManager.showScreen(ScreenState.MENU.name());
			}
		});
		add(bottomPanel,BorderLayout.SOUTH);
	}

}
