package client;

import java.awt.CardLayout;
import java.util.TreeMap;

import javax.swing.JPanel;

public class ScreenManager {

	private CardLayout layout;
	private JPanel rootScreen;
	
	public ScreenManager(MainScreen game) {
		layout = game.layout;
		rootScreen = game.rootScreen;

	}

	public void removeScreen(Screen s) {
		s.onClose();
		layout.removeLayoutComponent(s);
		
	}

	public void addScreen(Screen s, String name) {
		rootScreen.add(s, name);
	}
	
	public void showScreen(String name) {
		layout.show(rootScreen, name);
	}
	
	

}
