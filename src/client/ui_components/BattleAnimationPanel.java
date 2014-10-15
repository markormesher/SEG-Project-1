package client.ui_components;

public class BattleAnimationPanel extends AnimationPanel {

	// constants
	public static final int NORTH = 0,
			EAST = 90,
			SOUTH = 180,
			WEST = 270;

	public BattleAnimationPanel(int width, int height) {
		super(width, height);
	}

	// set this panel as a ship end section
	public void setAsShipFront(int orientation) {
		setObject("ship-front", orientation);
	}

	public void setAsShipBack(int orientation) {
		setObject("ship-back", orientation);
	}

	// set this panel as a ship middle section
	public void setAsShipMiddle(int orientation) {
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
		// TODO
	}
}
