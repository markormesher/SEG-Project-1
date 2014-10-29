package client;
import global.Settings;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class MainScreen extends JFrame{
	
	CardLayout layout = new CardLayout();
	final JPanel rootScreen = new JPanel(layout);
	
	ScreenManager screenManager = new ScreenManager(this);
	
	enum ScreenState {
				MENU,
				SETTING,
				GAME,
				HELP
	}
	public MainScreen() {
		super("BattleShips");
		rootScreen.add(new MenuScreen(screenManager),ScreenState.MENU.name());
		rootScreen.add(new SettingScreen(screenManager),ScreenState.SETTING.name());
		rootScreen.add(new HelpScreen(screenManager),ScreenState.HELP.name());
		
		
		add(rootScreen, BorderLayout.CENTER);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(Settings.RES_X,Settings.RES_Y);
		this.setPreferredSize(new Dimension(Settings.RES_X,Settings.RES_Y));
		this.setLocationByPlatform(true);
		this.setVisible(true);
		
	}
	
	public static void main(String[] args) {
		new MainScreen();
	}
}
