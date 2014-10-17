package client.ui_components;

import global.Settings;

import javax.swing.*;
import java.awt.*;

public class BattleBoard extends JPanel {

	// all of the cells on this board
	public BattleAnimationPanel[][] cells = new BattleAnimationPanel[Settings.GRID_SIZE][Settings.GRID_SIZE];

	public BattleBoard() {
        setOpaque(false);
		// enforce exactly the right size
		setPreferredSize(new Dimension(Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE));
		setSize(new Dimension(Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE));
		setMaximumSize(new Dimension(Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE));

		// a grid that holds the cells
		JPanel imageCellGrid = new JPanel();
        imageCellGrid.setOpaque(false);
		imageCellGrid.setBounds(0, 0, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE, Settings.IMAGE_CELL_SIZE * Settings.GRID_SIZE);
		imageCellGrid.setLayout(new GridLayout(Settings.GRID_SIZE, Settings.GRID_SIZE, 0, 0));
		add(imageCellGrid);
		this.setFocusable(true);

		// create a GRID_SIZE x GRID_SIZE grid of cells
		for (int y = 0; y < Settings.GRID_SIZE; ++y) {
			for (int x = 0; x < Settings.GRID_SIZE; ++x) {
				final BattleAnimationPanel panel = new BattleAnimationPanel(Settings.IMAGE_CELL_SIZE, Settings.IMAGE_CELL_SIZE);
				//panel.setBackground("bg", 0);
				cells[x][y] = panel;
				imageCellGrid.add(panel);
			}
		}

		// draw everything we made
		repaint();
	}

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.white);
        g2.setStroke(new BasicStroke(2));
        g.drawLine(2, 2, getWidth(), 2);
        g.drawLine(2,2,2,getHeight());
        g.drawLine(getWidth(),2,getWidth(),getHeight());
        g.drawLine(2,getHeight(),getWidth(),getHeight());
    }
}
