package client.ui;

public class BattleAnimationPanel extends AnimationPanel {

	// constants
	public static final int NORTH = 1,
			EAST = 2,
			SOUTH = 3,
			WEST = 4,
			HORIZONTAL = 5,
			VERTICAL = 6;

	public BattleAnimationPanel(int width, int height) {
		super(width, height);
	}

	// set this panel as a ship end section
	public void setAsShipEnd(int orientation) {
		// find rotation
		int angle;
		switch (orientation) {
			case NORTH:
				angle = 0;
				break;
			case EAST:
				angle = 90;
				break;
			case SOUTH:
				angle = 180;
				break;
			case WEST:
				angle = 270;
				break;
			default:
				angle = 0;
				break;
		}

		// little hack so rotation chooses the right image and angle
		// TODO: replace with 4-direction ship
		setObject(angle != 180 && angle != 270 ? "ship-front" : "ship-back", angle == 90 || angle == 270 ? 90 : 0);
	}

	// set this panel as a ship middle section
	public void setAsShipMiddle(int orientation) {
		// find rotation
		int angle;
		switch (orientation) {
			case VERTICAL:
				angle = 0;
				break;
			case HORIZONTAL:
			default:
				angle = 90;
				break;
		}

		// set image
		setObject("ship-middle", angle);
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
