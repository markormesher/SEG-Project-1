package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CountdownClock extends JPanel implements ActionListener{
    int seconds;
    double secondsLeft;
    public CountdownClock(int seconds){
        this.seconds = seconds;
        this.secondsLeft = seconds;
        //every 100 milliseconds update the clock
        Timer t = new Timer(100 , this);
        t.start();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;
        //for smooth lines
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.setColor(Color.black);
        //20 pixel padding
        graphics2D.drawOval(10, 10, getWidth() - 20, getHeight() - 20);
        double x1 = getWidth()/2.0;
        double y1 = getHeight()/2.0;

        //point on circle at the proportional angle
        double angle = 360.0 * ((double) (seconds-secondsLeft) / seconds);
        //System.out.println(angle);
        double x2 = Math.cos(Math.toRadians(angle)) * (getWidth() - 20)/2.0;
        double y2 = Math.sin(Math.toRadians(angle)) * (getHeight() - 20)/2.0;


        graphics2D.drawLine((int)x1,(int)y1,(int)(x2+x1),(int)(y2+y1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //one milliseconds ellapse therefore 0.1 seconds reduced
        secondsLeft-=0.1;
        //System.out.println(seconds);
        repaint();
    }
}
