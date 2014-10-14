package client.ui;

import javax.swing.*;
import java.awt.*;

public class AnimationPanel extends JLayeredPane {

	// theme settings
	private String theme = "default";

	// layers
	private JLabel background = new JLabel();
	private JLabel object = new JLabel();
	private JLabel effect = new JLabel();

	public AnimationPanel(int width, int height) {
		super();
		setPreferredSize(new Dimension(width, height));

		// position labels
		background.setLocation(0, 0);
		background.setSize(width, height);
		object.setLocation(0, 0);
		object.setSize(width, height);
		effect.setLocation(0, 0);
		effect.setSize(width, height);

		// place labels
		add(background, new Integer(0));
		add(object, new Integer(1));
		add(effect, new Integer(2));
	}

	// set the icon of a given label
	public void setIcon(JLabel label, String icon) {
		if (icon == null) {
			label.setIcon(null);
		} else {
			try {
				label.setIcon(new ImageIcon(AnimationPanel.class.getResource("/" + theme + "/" + icon)));
			} catch (NullPointerException e) {
				label.setIcon(null);
			}
		}
	}

	// set the current theme
	public void setTheme(String theme) {
		this.theme = theme == null ? "default" : theme;
	}

	// set the background of this panel
	public void setBackground(String newBackground) {
		setIcon(background, newBackground);
	}

	// set the object of this panel
	public void setObject(String newObject) {
		setIcon(object, newObject);
	}

	// set the effect image of this panel
	public void setEffect(String newEffect) {
		setIcon(effect, newEffect);
	}

}
