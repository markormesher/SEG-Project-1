package client.ui_components;

import global.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BattleBoardLocal extends JPanel {

	// size of the ship currently being place (nb. array pointer, NOT actual size)
	private int currentShipSizeIndex = 0;

	// current orientation
	private int currentOrientation = BattleAnimationPanel.NORTH;

	// board to hold all of the cells
	private BattleBoard board = new BattleBoard();

	// listeners for when placing ships is complete
	private ArrayList<ShipPlacementListener> shipPlacementListeners = new ArrayList<ShipPlacementListener>();

	// container to hold the ship the user is currently placing
	private JPanel activeShipContainer = new JPanel();

	// layered pane to hold the board layer and listener panel
	final JLayeredPane layeredPane = new JLayeredPane();

	// last cursor position (used to prevent jump when re-drawing the active ship)
	private int lastCursorX = 0;
	private int lastCursorY = 0;

	public BattleBoardLocal() {
		// set up this panel
		setLayout(null);
		setPreferredSize(new Dimension(Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE));
		setSize(new Dimension(Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE));

		// add a new layered pane to hold the board layer and listener panel
		layeredPane.setBounds(0, 0, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE);
		add(layeredPane);

		// create panel to lay over the board to listen for events
		final JPanel overlayPanel = new JPanel();
		overlayPanel.setLayout(null);
		overlayPanel.setOpaque(false);
		overlayPanel.setPreferredSize(new Dimension(Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE));
		overlayPanel.setSize(new Dimension(Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE));

		// container to hold the ship the user is currently placing
		activeShipContainer = new JPanel();
		activeShipContainer.setLayout(new BoxLayout(activeShipContainer, BoxLayout.Y_AXIS));
		activeShipContainer.setOpaque(false);

		// add the ship container to the overlay panel
		overlayPanel.add(activeShipContainer);

		// draw the current active ship
		redrawActiveShip();

		// mouse listener for movement/click events
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);

				// get mouse coordinates (adjust to centre image in cell)
				int x = e.getX() - (Settings.IMAGE_CELL_SIZE / 2);
				int y = e.getY() - (Settings.IMAGE_CELL_SIZE / 2);

				// save
				lastCursorX = x;
				lastCursorY = y;

				// position the active ship
				repositionActiveShip(x, y);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				// did we already finish?
				if (currentShipSizeIndex >= Settings.SHIP_SIZES.length) {
					return;
				}

				// rotate or place?
				if (SwingUtilities.isRightMouseButton(e)) {
					// rotate the active ship
					rotateActiveShip();
				} else {
					// get the cell coordinates of the click
					int x = (int) Math.floor(e.getX() / Settings.IMAGE_CELL_SIZE);
					int y = (int) Math.floor(e.getY() / Settings.IMAGE_CELL_SIZE);

					// get the size of the current ship
					int currentShipSize = Settings.SHIP_SIZES[currentShipSizeIndex];

					// figure out the direction in which we are going to add ship parts
					int orientationVectorX;
					int orientationVectorY;
					switch (currentOrientation) {
						case BattleAnimationPanel.NORTH:
							orientationVectorX = 0;
							orientationVectorY = 1;
							break;
						case BattleAnimationPanel.EAST:
							orientationVectorX = 1;
							orientationVectorY = 0;
							break;
						case BattleAnimationPanel.SOUTH:
							orientationVectorX = 0;
							orientationVectorY = 1;
							break;
						case BattleAnimationPanel.WEST:
							orientationVectorX = 1;
							orientationVectorY = 0;
							break;
						default:
							orientationVectorX = 0;
							orientationVectorY = 1;
							break;
					}

					// figure out the target locations of each new parts (stored as x/y pairs)
					int[][] targets = new int[currentShipSize][2];
					for (int i = 0; i < currentShipSize; ++i) {
						targets[i][0] = x + (orientationVectorX * i);
						targets[i][1] = y + (orientationVectorY * i);
					}

					// prevent out-of-bounds ships by checking that every target is on the grid
					for (int[] t : targets) {
						if (t[0] < 0 || t[0] >= Settings.GRID_SIZE) return;
						if (t[1] < 0 || t[1] >= Settings.GRID_SIZE) return;
					}

					// collision detection
					for (int[] t : targets) {
						if (!board.cells[t[0]][t[1]].isEmpty()) return;
					}

					// set the tile closest to the cursor
					if (currentOrientation == BattleAnimationPanel.NORTH || currentOrientation == BattleAnimationPanel.WEST) {
						board.cells[targets[0][0]][targets[0][1]].setAsShipFront(currentOrientation);
					} else {
						board.cells[targets[0][0]][targets[0][1]].setAsShipBack(currentOrientation);
					}

					// set the middle tiles
					for (int i = 1; i < currentShipSize - 1; ++i) {
						board.cells[targets[i][0]][targets[i][1]].setAsShipMiddle(currentOrientation);
					}

					// set the tile furthest from the cursor
					if (currentOrientation == BattleAnimationPanel.NORTH || currentOrientation == BattleAnimationPanel.WEST) {
						board.cells[targets[currentShipSize - 1][0]][targets[currentShipSize - 1][1]].setAsShipBack(currentOrientation);
					} else {
						board.cells[targets[currentShipSize - 1][0]][targets[currentShipSize - 1][1]].setAsShipFront(currentOrientation);
					}

					// move to the next ship size
					currentShipSizeIndex++;

					// have we finished adding ships?
					if (currentShipSizeIndex == Settings.SHIP_SIZES.length) {
						activeShipContainer.removeAll();
						for (ShipPlacementListener listener : shipPlacementListeners) {
							listener.onFinished();
						}
						return;
					}

					// redraw the active ship
					redrawActiveShip();
				}
			}
		};

		// add mouse listeners
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);

		// make sure listeners work
		setFocusable(true);
		requestFocusInWindow();

		// add the listener layer over the board
		layeredPane.add(board, new Integer(0));
		layeredPane.add(overlayPanel, new Integer(1));
	}

	private void rotateActiveShip() {
		// switch to new rotation
		int newOrientation;
		switch (currentOrientation) {
			case BattleAnimationPanel.NORTH:
				newOrientation = BattleAnimationPanel.EAST;
				break;
			case BattleAnimationPanel.EAST:
				newOrientation = BattleAnimationPanel.SOUTH;
				break;
			case BattleAnimationPanel.SOUTH:
				newOrientation = BattleAnimationPanel.WEST;
				break;
			case BattleAnimationPanel.WEST:
				newOrientation = BattleAnimationPanel.NORTH;
				break;
			default:
				newOrientation = BattleAnimationPanel.NORTH;
				break;
		}
		currentOrientation = newOrientation;

		// redraw the active ship
		redrawActiveShip();
	}

	private void redrawActiveShip() {
		// start afresh
		activeShipContainer.removeAll();

		// are we finished placing ships?
		if (currentShipSizeIndex >= Settings.SHIP_SIZES.length) return;

		// set the right orientation
		if (currentOrientation == BattleAnimationPanel.NORTH || currentOrientation == BattleAnimationPanel.SOUTH) {
			activeShipContainer.setLayout(new BoxLayout(activeShipContainer, BoxLayout.Y_AXIS));
		} else {
			activeShipContainer.setLayout(new BoxLayout(activeShipContainer, BoxLayout.X_AXIS));
		}

		// add the item closest to the cursor
		BattleAnimationPanel shipStart = new BattleAnimationPanel(Settings.IMAGE_CELL_SIZE, Settings.IMAGE_CELL_SIZE);
		if (currentOrientation == BattleAnimationPanel.NORTH || currentOrientation == BattleAnimationPanel.WEST) {
			shipStart.setAsShipFront(currentOrientation);
		} else {
			shipStart.setAsShipBack(currentOrientation);
		}
		activeShipContainer.add(shipStart, 0);

		// load in middle sections
		for (int i = 0; i < Settings.SHIP_SIZES[currentShipSizeIndex] - 2; ++i) {
			BattleAnimationPanel shipMiddle = new BattleAnimationPanel(Settings.IMAGE_CELL_SIZE, Settings.IMAGE_CELL_SIZE);
			shipMiddle.setAsShipMiddle(currentOrientation);
			activeShipContainer.add(shipMiddle, i + 1);
		}

		// add the item furthest from the cursor
		BattleAnimationPanel shipEnd = new BattleAnimationPanel(Settings.IMAGE_CELL_SIZE, Settings.IMAGE_CELL_SIZE);
		if (currentOrientation == BattleAnimationPanel.NORTH || currentOrientation == BattleAnimationPanel.WEST) {
			shipEnd.setAsShipBack(currentOrientation);
		} else {
			shipEnd.setAsShipFront(currentOrientation);
		}
		activeShipContainer.add(shipEnd, Settings.SHIP_SIZES[currentShipSizeIndex] - 1);

		// redraw all of this
		activeShipContainer.revalidate();
		activeShipContainer.repaint();
		layeredPane.repaint();

		// reposition the ship
		repositionActiveShip(lastCursorX, lastCursorY);
	}

	private void repositionActiveShip(int x, int y) {
		// work out ship boundaries
		int shipWidth;
		int shipHeight;
		if (currentOrientation == BattleAnimationPanel.NORTH || currentOrientation == BattleAnimationPanel.SOUTH) {
			shipWidth = Settings.IMAGE_CELL_SIZE;
			shipHeight = activeShipContainer.getComponentCount() * Settings.IMAGE_CELL_SIZE;
		} else {
			shipWidth = activeShipContainer.getComponentCount() * Settings.IMAGE_CELL_SIZE;
			shipHeight = Settings.IMAGE_CELL_SIZE;
		}
		activeShipContainer.setBounds(x, y, shipWidth, shipHeight);

		// redraw
		layeredPane.repaint();
	}

	public BattleAnimationPanel[][] getBoardCells() {
		return board.cells;
	}

	public void addShipPlacementListener(ShipPlacementListener listener) {
		shipPlacementListeners.add(listener);
	}

	public interface ShipPlacementListener {

		public void onFinished();
	}

}
