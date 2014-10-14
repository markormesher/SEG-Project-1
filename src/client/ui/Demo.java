package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Demo extends JFrame {

	public static String[] themes = {"default"};
	public static String[] bgs = {null, "bg1", "bg2"};
	public static String[] objects = {null, "ship-end", "ship-middle", "pin-miss", "pin-hit"};
	public static int[] rotations = {0, 90, 180, 270};

	int currentTheme = 0;
	int currentBg = 0;
	int currentObject = 0;
	int currentRotation = 0;

	public static void main(String[] args) {
		Demo demo = new Demo();
		demo.setVisible(true);
	}

	public Demo() {
		// basic setup
		super("Animated Panel Demo");
		setSize(new Dimension(600, 200));
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// create buttons
		JPanel buttons = new JPanel(new FlowLayout());
		JButton cycleTheme = new JButton("Cycle Theme");
		buttons.add(cycleTheme);
		JButton cycleBackground = new JButton("Cycle BG");
		buttons.add(cycleBackground);
		JButton cycleObject = new JButton("Cycle Object");
		buttons.add(cycleObject);
		final JButton rotateObject = new JButton("Rotate Object");
		buttons.add(rotateObject);

		// create animated panel
		final AnimationPanel animatedPanel = new AnimationPanel(32, 32);
		animatedPanel.setTheme(themes[currentTheme]);
		animatedPanel.setBackground(bgs[currentBg], 0);
		animatedPanel.setObject(objects[currentObject], 0);

		// add to main window
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(animatedPanel, BorderLayout.CENTER);
		getContentPane().add(buttons, BorderLayout.SOUTH);

		// listeners
		cycleTheme.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				++currentTheme;
				if (currentTheme == themes.length) currentTheme = 0;
				animatedPanel.setTheme(themes[currentTheme]);
			}
		});
		cycleBackground.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				++currentBg;
				if (currentBg == bgs.length) currentBg = 0;
				animatedPanel.setBackground(bgs[currentBg], 0);
			}
		});
		cycleObject.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				++currentObject;
				if (currentObject == objects.length) currentObject = 0;
				animatedPanel.setObject(objects[currentObject], rotations[currentRotation]);
			}
		});
		rotateObject.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				++currentRotation;
				if (currentRotation == rotations.length) currentRotation = 0;
				animatedPanel.setObject(objects[currentObject], rotations[currentRotation]);
			}
		});
	}
}