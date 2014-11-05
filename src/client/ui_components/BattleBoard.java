package client.ui_components;

import global.Settings;

import javax.swing.*;
import java.awt.*;

public class BattleBoard extends JPanel {

	// all of the cells on this board
	public BattleAnimationPanel[][] cells = new BattleAnimationPanel[Settings.GRID_SIZE][Settings.GRID_SIZE];

	public BattleBoard() {
		// set up grid layout
		super(new GridLayout(Settings.GRID_SIZE, Settings.GRID_SIZE));
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setFocusable(true);

		// enforce exactly the right sizes
		setPreferredSize(new Dimension(Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE));
		setSize(new Dimension(Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE));
		setMaximumSize(new Dimension(Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE));


		// create a GRID_SIZE x GRID_SIZE grid of cells
		for (int y = 0; y < Settings.GRID_SIZE; ++y) {
			for (int x = 0; x < Settings.GRID_SIZE; ++x) {
				BattleAnimationPanel panel = new BattleAnimationPanel(Settings.IMAGE_CELL_SIZE, Settings.IMAGE_CELL_SIZE);
				panel.setBackground("bg", 0);
				cells[x][y] = panel;
				add(panel);
			}
		}

		// draw everything we made
		repaint();
	}

	// draw a white border around the board
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.white);
		g2.setStroke(new BasicStroke(2));
		g2.drawLine(0, 0, getWidth(), 0);
		g2.drawLine(0, 0, 0, getHeight());
		g2.drawLine(getWidth(), 0, getWidth(), getHeight());
		g2.drawLine(0, getHeight(), getWidth(), getHeight());
	}
}
