package client.ui_components;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class CountdownClock extends JPanel implements ActionListener {

	// clock info
	int totalSeconds;
	double secondsLeft;

	// main image
	private BufferedImage image;

	// listeners
	private ArrayList<TimeoutListener> timeoutListeners = new ArrayList<TimeoutListener>();

	// update the clock every 100 milliseconds
	private Timer timer = new Timer(100, this);

	public CountdownClock(int seconds) {
		this.totalSeconds = seconds;
		this.secondsLeft = seconds;
		try {
			image = ImageIO.read(this.getClass().getResource("/images/clock.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D graphics2D = (Graphics2D) g;

		// for smooth lines
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// 20 pixel padding
		graphics2D.drawOval(10, 10, getWidth() - 20, getHeight() - 20);

		// rotate image to appropriate position
		double angle = 360.0 * (totalSeconds - secondsLeft) / totalSeconds;
		Double rotate = angle * (Math.PI / 180);
		graphics2D.rotate(rotate, getWidth() / 2.0, getHeight() / 2.0);
		graphics2D.drawImage(image, 0, 0, getWidth(), getHeight(), null);
	}

	// triggered every 100 milliseconds (0.1 second)
	@Override
	public void actionPerformed(ActionEvent e) {
		secondsLeft -= 0.1;
		//when it hits 0 , trigger the timeout event and stop
		if (secondsLeft <= 0) {
			secondsLeft = totalSeconds;
			for (TimeoutListener listener : timeoutListeners) listener.onTimeout();
			timer.stop();
		}
		repaint();
	}

	public void start() {
		timer.start();
	}

	public void stop() {
		secondsLeft = totalSeconds;
		timer.stop();
		repaint();
	}

	// register a new timeout listener
	public void addTimeoutListener(TimeoutListener listener) {
		timeoutListeners.add(listener);
	}

	public interface TimeoutListener {

		void onTimeout();
	}
}
