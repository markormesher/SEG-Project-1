package client.ui_components;

import global.Settings;

import javax.swing.*;
import java.awt.*;

public class BattleBoard extends JPanel {

	// all of the cells on this board
	public BattleAnimationPanel[][] cells = new BattleAnimationPanel[Settings.GRID_SIZE][Settings.GRID_SIZE];

	public BattleBoard() {
        setBackground(Color.white);
		// enforce exactly the right size
		setPreferredSize(new Dimension(Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE));
		setSize(new Dimension(Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE));
		setMaximumSize(new Dimension(Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE));

		// a grid that holds the cells
		JPanel imageCellGrid = new JPanel();
		imageCellGrid.setBounds(0, 0, getWidth(), getHeight());
		imageCellGrid.setLayout(new GridLayout(Settings.GRID_SIZE, Settings.GRID_SIZE, 0, 0));
		add(imageCellGrid);
		this.setFocusable(true);

		// create a GRID_SIZE x GRID_SIZE grid of cells
		for (int y = 0; y < Settings.GRID_SIZE; ++y) {
			for (int x = 0; x < Settings.GRID_SIZE; ++x) {
				final BattleAnimationPanel panel = new BattleAnimationPanel(Settings.IMAGE_CELL_SIZE, Settings.IMAGE_CELL_SIZE);
				panel.setBackground("bg", 0);
				cells[x][y] = panel;
				imageCellGrid.add(panel);
			}
		}

		// draw everything we made
		repaint();
	}
}
