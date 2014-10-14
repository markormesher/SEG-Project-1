package client.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
	public void setIcon(final JLabel label, String icon, final int angle) {
		if (theme == null || icon == null) {
			label.setIcon(null);
		} else {
			try {
				// read the image from resources
				final BufferedImage image = ImageIO.read(AnimationPanel.class.getResource("/images/" + theme + "/" + icon + ".png"));
				// set the label to a new icon
				label.setIcon(new ImageIcon() {
					@Override
					public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
						super.paintIcon(c, g, x, y);
						Graphics2D g2 = (Graphics2D) g;
						Double rotate = angle * (Math.PI / 180);
						g2.rotate(rotate, (label.getWidth() / 2), (label.getHeight() / 2));
						g2.drawImage(image, 0, 0, null);
					}
				});
			} catch (IOException e) {
				label.setIcon(null);
			} catch (NullPointerException e) {
				label.setIcon(null);
			} catch (IllegalArgumentException e) {
				label.setIcon(null);
			}
		}
	}

	// set the current theme
	public void setTheme(String theme) {
		this.theme = theme == null ? "default" : theme;
	}

	// set the background of this panel
	public void setBackground(String newBackground, int angle) {
		setIcon(background, newBackground, angle);
	}

	// set the object of this panel
	public void setObject(String newObject, int angle) {
		setIcon(object, newObject, angle);
	}

	// set the effect image of this panel
	public void setEffect(String newEffect, int angle) {
		setIcon(effect, newEffect, angle);
	}

}
