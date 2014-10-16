package client.ui_components;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BattleAnimationPanel extends AnimationPanel implements ActionListener {

	// constants
	public static final int NORTH = 0,
			EAST = 90,
			SOUTH = 180,
			WEST = 270;

	// is there a ship here?
	private boolean empty = true;

	// for animations
	private int animationSpeed = 60;
	private Timer animationTimer;
	private int currentExplosion = 0;
	private int totalExplosions = 14;

	public BattleAnimationPanel(int width, int height) {
		super(width, height);
	}

	// set this panel as a ship end section
	public void setAsShipFront(int orientation) {
		empty = false;
		setObject("ship-front", orientation);
	}

	public void setAsShipBack(int orientation) {
		empty = false;
		setObject("ship-back", orientation);
	}

	// set this panel as a ship middle section
	public void setAsShipMiddle(int orientation) {
		empty = false;
		setObject("ship-middle", orientation);
	}

	// set this panel as a "hit" pin
	public void setAsHitPin() {
		setObject("pin-hit", 0);
	}

	// set this panel as a "miss" pin
	public void setAsMissPin() {
		setObject("pin-miss", 0);
	}

	// run the explosion animation on this panel
	public void explode() {
		currentExplosion = 1;
		animationTimer = new Timer(animationSpeed, this);
		animationTimer.start();
	}

	// TODO: animation for "miss" (Mark)

	// is this cell still empty?
	public boolean isEmpty() {
		return empty;
	}

	// animation listener

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		// exploding?
		if (currentExplosion > 0) {
			// set the explosion image
			setEffect("explosion-" + currentExplosion, 0);
			currentExplosion++;
			// finished?
			if (currentExplosion > totalExplosions) {
				currentExplosion = 0;
				if (animationTimer != null) animationTimer.stop();
				setObject("debris", 0);
				setEffect(null, 0);
			}
		}
	}
}
