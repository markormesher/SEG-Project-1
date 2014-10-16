package client.ui_components;

import server.ClientConnectedListener;
import server.ClientDisconnectedListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class CountdownClock extends JPanel implements ActionListener {

	int totalSeconds;
	double secondsLeft;

    BufferedImage image ;

    // update the clock every 100 milliseconds
    public Timer timer = new Timer(100, this);

	public CountdownClock(int seconds) {
		this.totalSeconds = seconds;
		this.secondsLeft = seconds;
        try {
            image= ImageIO.read(this.getClass().getResource("/images/clock.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

		//timer.start();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D graphics2D = (Graphics2D) g;

		// for smooth lines
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// 20 pixel padding
		graphics2D.drawOval(10, 10, getWidth() - 20, getHeight() - 20);

		// find centre point
		double x1 = getWidth() / 2.0;
		double y1 = getHeight() / 2.0;

		// point on circle at the proportional angle
		double angle = 360.0 * (totalSeconds - secondsLeft) / totalSeconds;
		double x2 = Math.cos(Math.toRadians(angle)) * (getWidth() - 20) / 2.0;
		double y2 = Math.sin(Math.toRadians(angle)) * (getHeight() - 20) / 2.0;

		// paint line
		graphics2D.setColor(Color.black);
		graphics2D.drawLine((int) x1, (int) y1, (int) (x2 + x1), (int) (y2 + y1));

        Double rotate = angle * (Math.PI / 180);
        graphics2D.rotate(rotate, getWidth()/2.0, getHeight()/2.0);
        graphics2D.drawImage(image, 0, 0,getWidth(),getHeight(), null);
	}

	// triggered every 100 milliseconds (0.1 second)
	@Override
	public void actionPerformed(ActionEvent e) {
		secondsLeft -= 0.1;
        //when it hits 0 , trigger the timeout event and stop
        if(secondsLeft <= 0){
            secondsLeft = totalSeconds;
            for(TimeoutListener listener:timeoutListeners) listener.onTimeout();
            timer.stop();
        }
		repaint();
	}

    public void start(){
        timer.start();
    }
    public void stop(){
        secondsLeft = totalSeconds;
        timer.stop();
    }


    public interface TimeoutListener{
        void onTimeout();
    }

    ArrayList<TimeoutListener> timeoutListeners = new ArrayList<TimeoutListener>();

    // register a new timeout listener
    public void addTimeoutListener(TimeoutListener listener){
        timeoutListeners.add(listener);
    }

}
