package client.ui;

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
	final BattleBoard board = new BattleBoard();

	// listeners for when placing ships is complete
	ArrayList<ShipPlacementListener> shipPlacementListeners = new ArrayList<ShipPlacementListener>();

	public BattleBoardLocal() {
		// set up this panel
		setLayout(null);
		setPreferredSize(new Dimension(Settings.IMAGE_CELL_SIZE * 10, Settings.IMAGE_CELL_SIZE * 10));
		setSize(new Dimension(Settings.IMAGE_CELL_SIZE * 10, Settings.IMAGE_CELL_SIZE * 10));
		setFocusable(true);

		// add a new layered pane to hold the board layer and listener panel
		final JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, Settings.IMAGE_CELL_SIZE * 10, Settings.IMAGE_CELL_SIZE * 10);
		add(layeredPane);

		// create panel to lay over the board to listen for events
		final JPanel overlayPanel = new JPanel();
		overlayPanel.setOpaque(false);
		overlayPanel.setPreferredSize(new Dimension(Settings.IMAGE_CELL_SIZE * 10, Settings.IMAGE_CELL_SIZE * 10));
		overlayPanel.setSize(new Dimension(Settings.IMAGE_CELL_SIZE * 10, Settings.IMAGE_CELL_SIZE * 10));

		// container to hold the ship the user is currently placing
		final JPanel activeShipContainer = new JPanel();
		activeShipContainer.setLayout(new BoxLayout(activeShipContainer, BoxLayout.Y_AXIS));
		activeShipContainer.setOpaque(false);

		// add the ship container to the overlay panel, but hide the panel
		overlayPanel.add(activeShipContainer);
		overlayPanel.setVisible(false);

		// pre-create the front and back end of a ship - these won't change
		final BattleAnimationPanel shipFront = new BattleAnimationPanel(Settings.IMAGE_CELL_SIZE, Settings.IMAGE_CELL_SIZE);
		shipFront.setAsShipFront(currentOrientation);
		final BattleAnimationPanel shipBack = new BattleAnimationPanel(Settings.IMAGE_CELL_SIZE, Settings.IMAGE_CELL_SIZE);
		shipBack.setAsShipBack(currentOrientation);

		// pre-populate the active ship container with the first ship to be placed
		activeShipContainer.add(shipFront);
		for (int i = 0; i < Settings.SHIP_SIZES[currentShipSizeIndex] - 2; ++i) {
			BattleAnimationPanel shipMiddle = new BattleAnimationPanel(Settings.IMAGE_CELL_SIZE, Settings.IMAGE_CELL_SIZE);
			shipMiddle.setAsShipMiddle(currentOrientation);
			activeShipContainer.add(shipMiddle);
		}
		activeShipContainer.add(shipBack);

		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				overlayPanel.setVisible(true);

				// get mouse coordinates
				int x1 = e.getX() - 18;
				int y1 = e.getY() - 9;

				// position the active ship
				activeShipContainer.setBounds(x1, y1, Settings.IMAGE_CELL_SIZE, activeShipContainer.getComponentCount() * Settings.IMAGE_CELL_SIZE);

				// redraw
				layeredPane.repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int x = (int) Math.floor(e.getX() / Settings.IMAGE_CELL_SIZE);
				int y = (int) Math.floor(e.getY() / Settings.IMAGE_CELL_SIZE);

				// TODO: prevent overflows

				// TODO: collision detection

				// get the size of the current ship
				int currentShipSize = Settings.SHIP_SIZES[BattleBoardLocal.this.currentShipSizeIndex];

				// set the actual tiles on the board
				board.tiles[x][y].setAsShipFront(BattleAnimationPanel.NORTH);
				for (int i = 0; i < currentShipSize - 2; ++i) {
					board.tiles[x][y + i + 1].setAsShipMiddle(currentOrientation);
				}
				board.tiles[x][y + currentShipSize - 1].setAsShipBack(BattleAnimationPanel.NORTH);

				// move to the next ship size
				BattleBoardLocal.this.currentShipSizeIndex++;

				// have we finished adding ships?
				if (BattleBoardLocal.this.currentShipSizeIndex == Settings.SHIP_SIZES.length) {
					activeShipContainer.removeAll();
					for (ShipPlacementListener listener : shipPlacementListeners) {
						listener.onFinished();
					}
					return;
				}

				// move on to the next size
				currentShipSize = Settings.SHIP_SIZES[currentShipSizeIndex];

				// remove everything but the front
				activeShipContainer.remove(shipBack);
				for (Component c : activeShipContainer.getComponents()) {
					if (c != shipFront) activeShipContainer.remove(c);
				}

				// re-add the middle sections and the back
				for (int i = 0; i < currentShipSize - 2; ++i) {
					BattleAnimationPanel shipMiddle = new BattleAnimationPanel(Settings.IMAGE_CELL_SIZE, Settings.IMAGE_CELL_SIZE);
					shipMiddle.setAsShipMiddle(currentOrientation);
					activeShipContainer.add(shipMiddle);
				}
				activeShipContainer.add(shipBack);
			}
		};

		// add mouse listeners
		board.addMouseListener(mouseAdapter);
		board.addMouseMotionListener(mouseAdapter);

		// add the listener layer over the board
		layeredPane.add(board, new Integer(0));
		layeredPane.add(overlayPanel, new Integer(1));
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
