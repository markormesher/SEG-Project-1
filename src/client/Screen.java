package client;


import javax.swing.JPanel;

public class Screen extends JPanel {

	protected ScreenManager screenManager;
	protected boolean isPopup;

	public Screen(ScreenManager sm) {
		super();
		this.screenManager = sm;
	}

	public void onClose() {

	}
	
	public ScreenManager getScreenManager() {
		return screenManager;
	}

}
