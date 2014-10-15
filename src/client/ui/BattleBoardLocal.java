package client.ui;

import global.Settings;
import org.omg.CORBA.BAD_CONTEXT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Set;

public class BattleBoardLocal extends JPanel implements ActionListener {

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

				// get mouse coordinates
				int x = e.getX() - (Settings.IMAGE_CELL_SIZE / 2);
				int y = e.getY() - (Settings.IMAGE_CELL_SIZE / 2);

				// position the active ship
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

					// TODO: collision detection
					// TODO: prevent overflows

					// set the tile closest to the cursor
					if (currentOrientation == BattleAnimationPanel.NORTH || currentOrientation == BattleAnimationPanel.WEST) {
						board.tiles[targets[0][0]][targets[0][1]].setAsShipFront(currentOrientation);
					} else {
						board.tiles[targets[0][0]][targets[0][1]].setAsShipBack(currentOrientation);
					}

					// set the middle tiles
					for (int i = 1; i < currentShipSize - 1; ++i) {
						board.tiles[targets[i][0]][targets[i][1]].setAsShipMiddle(currentOrientation);
					}

					// set the tile furthest from the cursor
					if (currentOrientation == BattleAnimationPanel.NORTH || currentOrientation == BattleAnimationPanel.WEST) {
						board.tiles[targets[currentShipSize - 1][0]][targets[currentShipSize - 1][1]].setAsShipBack(currentOrientation);
					} else {
						board.tiles[targets[currentShipSize - 1][0]][targets[currentShipSize - 1][1]].setAsShipFront(currentOrientation);
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

		// DEBUG: rotate every 10 secs
		Timer t = new Timer(10000, this);
		t.start();

		// make sure listeners work
		setFocusable(true);
		requestFocusInWindow();

		// add the listener layer over the board
		layeredPane.add(board, new Integer(0));
		layeredPane.add(overlayPanel, new Integer(1));
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		rotateActiveShip();
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
	}

	public BattleAnimationPanel[][] getTiles() {
		return board.tiles;
	}

	public void addShipPlacementListener(ShipPlacementListener listener) {
		shipPlacementListeners.add(listener);
	}

	public interface ShipPlacementListener {

		public void onFinished();
	}

}
