package client.ui_components;

import client.BattleClientGui;
import global.Result;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ResultsWindow extends JFrame {

	// view components
	private JPanel innerGrid;
	private Font font;
    private JFrame clientFrame;

	public ResultsWindow(Result result, Result opponentResult, final BattleClientGui clientFrame) {
		// create the font
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/PressStart2P.ttf"));
			font = font.deriveFont(25f);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// reference to the BattleClientGui (don't link) to dispose of when opening new game
		this.clientFrame = clientFrame;

		// set up window
		setSize(350, 350);
		setResizable(false);
		getContentPane().setBackground(Color.white);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		// labels
		JLabel resultLabel = new JLabel(result.won ? "YOU WON" : "YOU LOST");
		resultLabel.setForeground(result.won ? Color.GREEN : Color.RED);
		resultLabel.setFont(font.deriveFont(35f));
		resultLabel.setBorder(new EmptyBorder(new Insets(12, 12, 12, 12)));
		resultLabel.setHorizontalAlignment(JLabel.HORIZONTAL);
		add(resultLabel, BorderLayout.NORTH);

		// 3 columns wide grid
		innerGrid = new JPanel(new GridLayout(0, 3, 5, 5));
		innerGrid.setBackground(Color.white);

		// your names
		JLabel yourName = new JLabel(result.username);
		yourName.setFont(font.deriveFont(20f));
		yourName.setHorizontalAlignment(JLabel.HORIZONTAL);
		innerGrid.add(yourName);

		// needed for the empty middle cell in the first row
		JPanel emptyPanel = new JPanel();
		emptyPanel.setOpaque(false);
		innerGrid.add(emptyPanel);

		// opponent
		JLabel opponentName = new JLabel(opponentResult.username);
		opponentName.setFont(font.deriveFont(20f));
		opponentName.setHorizontalAlignment(JLabel.HORIZONTAL);
		innerGrid.add(opponentName);

		// stats
		addRow(String.valueOf(result.totalShots), "SHOTS", String.valueOf(opponentResult.totalShots));
		addRow(String.valueOf(result.misses), "MISSES", String.valueOf(opponentResult.misses));
		addRow(String.valueOf(result.hits), "HITS", String.valueOf(opponentResult.hits));

		// percentages rounded up
		addRow(String.valueOf(Math.round((double) result.hits / result.totalShots * 100)) + "%",
				"HIT %", String.valueOf(Math.round((double) opponentResult.hits / opponentResult.totalShots * 100)) + "%");
		add(innerGrid, BorderLayout.CENTER);

		// actions
		JButton play = new JButton("Play again");
		play.setFont(font.deriveFont(25f));
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new BattleClientGui().setVisible(true);
				clientFrame.disconnect();
				dispose();
			}
		});
		add(play, BorderLayout.SOUTH);

		// center on screen
		setLocationRelativeTo(null);
	}

	public void addRow(String left, String middle, String right) {
		JLabel myTotal = new JLabel(left);
		myTotal.setFont(font.deriveFont(15f));
		myTotal.setHorizontalAlignment(JLabel.HORIZONTAL);
		innerGrid.add(myTotal);
		JLabel totalLabel = new JLabel(middle);
		totalLabel.setFont(font.deriveFont(13f));
		totalLabel.setHorizontalAlignment(JLabel.HORIZONTAL);
		innerGrid.add(totalLabel);
		JLabel opponentTotal = new JLabel(right);
		opponentTotal.setFont(font.deriveFont(15f));
		opponentTotal.setHorizontalAlignment(JLabel.HORIZONTAL);
		innerGrid.add(opponentTotal);
	}

}
