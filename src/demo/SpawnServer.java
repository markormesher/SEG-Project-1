package demo;

import server.BattleServerControlPanel;

import javax.swing.*;

public class SpawnServer {

	public static void main(String[] args) {
		// make the UI fit the OS defaults
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception useDefault) {
					// at least we tried - use default layout
				}
				BattleServerControlPanel bscp = new BattleServerControlPanel();
				bscp.init();
			}
		});
	}

}
