package client;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class MenuScreen extends Screen {

	public MenuScreen(ScreenManager sm) {
		super(sm);
		setLayout(new BorderLayout());
		
		JPanel topPanel = new JPanel();

		JLabel title = new JLabel();
		title.setIcon(new ImageIcon(this.getClass().getResource("/images/logo.png")));
		topPanel.add(title);
		
		add(topPanel, BorderLayout.NORTH);
		
		JButton findGameButton = new JButton("Find a Game");
		JButton settingButton = new JButton("Settings");
		JButton helpButton = new JButton("Help");
		JButton exitButton = new JButton("Quit");

	
		settingButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				screenManager.showScreen(MainScreen.ScreenState.SETTING.name());
			}
		});
		
		findGameButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				screenManager.addScreen(new BattleClientGui(screenManager), MainScreen.ScreenState.GAME.name());
				screenManager.showScreen(MainScreen.ScreenState.GAME.name());
			}
		});
		
		helpButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				screenManager.showScreen(MainScreen.ScreenState.HELP.name());
			}
		});
		
		JPanel outerButtonPanel = new JPanel(new BorderLayout());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		
		buttonPanel.add(Box.createVerticalGlue());
		buttonPanel.add(findGameButton);
		findGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		buttonPanel.add(Box.createVerticalGlue());
		buttonPanel.add(settingButton);
		settingButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		buttonPanel.add(Box.createVerticalGlue());
		buttonPanel.add(helpButton);
		helpButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		buttonPanel.add(Box.createVerticalGlue());
		buttonPanel.add(exitButton);
		exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		buttonPanel.add(Box.createVerticalGlue());
		outerButtonPanel.add(buttonPanel, BorderLayout.CENTER);
		add(outerButtonPanel, BorderLayout.CENTER);
		
	}

}
