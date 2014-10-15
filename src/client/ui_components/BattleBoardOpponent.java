package client.ui_components;

import global.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BattleBoardOpponent extends JPanel {

	// board to hold all of the cells
	private BattleBoard board = new BattleBoard();

	// shot listeners for this board
	ArrayList<ShotListener> shotListeners = new ArrayList<ShotListener>();

	public BattleBoardOpponent() {
		// set up this panel
		setLayout(null);
		setPreferredSize(new Dimension(Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE));
		setSize(new Dimension(Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE));

		// add a new layered pane to hold the board layer and listener panel
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE);
		add(layeredPane);

		// create panel to lay over the board to listen for events
		final JPanel overlayPanel = new JPanel();
		overlayPanel.setOpaque(false);
		overlayPanel.setPreferredSize(new Dimension(Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE));
		overlayPanel.setSize(new Dimension(Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE));

		// listen for mouse events on the overlay board
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				// find the cell coordinate of the click
				int x = (int) Math.floor(e.getX() / Settings.IMAGE_CELL_SIZE);
				int y = (int) Math.floor(e.getY() / Settings.IMAGE_CELL_SIZE);
				for (ShotListener listener : shotListeners) {
					listener.onShotFired(x, y);
				}
			}
		};
		board.addMouseListener(mouseAdapter);

		// add the listener layer over the board
		layeredPane.add(board, new Integer(0));
		layeredPane.add(overlayPanel, new Integer(1));
	}

	// get the titles of the battle board
	public BattleAnimationPanel[][] getBoardCells() {
		return board.cells;
	}

	// add a shot listener to this board
	public void addShotListener(ShotListener listener) {
		shotListeners.add(listener);
	}

	interface ShotListener {
		void onShotFired(int x, int y);
	}
}