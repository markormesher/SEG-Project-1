package demo;

import client.BattleClientGui;

import javax.swing.*;

public class SpawnClient {

	public static void main(String[] args) {
		// make the UI fit the OS defaults
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception useDefault) {
					// at least we tried - use default layout
				}
				BattleClientGui bcg = new BattleClientGui();
				bcg.setVisible(true);
			}
		});
	}

}
