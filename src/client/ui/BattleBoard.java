package client.ui;

import global.Settings;

import javax.swing.*;
import java.awt.*;

public class BattleBoard extends JPanel {

	// all of the cells on this board
	public BattleAnimationPanel[][] tiles = new BattleAnimationPanel[10][10];

	public BattleBoard() {
		// enforce exactly the right size
		setPreferredSize(new Dimension(Settings.IMAGE_CELL_SIZE * 10, Settings.IMAGE_CELL_SIZE * 10));
		setSize(new Dimension(Settings.IMAGE_CELL_SIZE * 10, Settings.IMAGE_CELL_SIZE * 10));
		setMaximumSize(new Dimension(Settings.IMAGE_CELL_SIZE * 10, Settings.IMAGE_CELL_SIZE * 10));

		// a grid that holds the cells
		JPanel imageCellGrid = new JPanel();
		imageCellGrid.setBounds(0, 0, getWidth(), getHeight());
		imageCellGrid.setLayout(new GridLayout(10, 10, 0, 0));
		add(imageCellGrid);
		this.setFocusable(true);

		// create a 10 x 10 grid of cells
		for (int y = 0; y < 10; ++y) {
			for (int x = 0; x < 10; ++x) {
				final BattleAnimationPanel panel = new BattleAnimationPanel(Settings.IMAGE_CELL_SIZE, Settings.IMAGE_CELL_SIZE);
				panel.setBackground("bg", 0);
				tiles[x][y] = panel;
				imageCellGrid.add(panel);
			}
		}

		// draw everything we made
		repaint();
	}
}
