package client.ui;


import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class BattleBoard extends JPanel {
    static final int unitSize = 32;

    public static String[] bgs = {null, "bg1", "bg2"};
    public static String[] objects = {null, "ship-end", "ship-middle", "pin-miss", "pin-hit"};

    //the tiles are in this array
    public BattleAnimationPanel[][] tiles = new BattleAnimationPanel[10][10];

    boolean isHorizontal = false;
    int c = 0;
    public BattleBoard(){
        setPreferredSize(new Dimension(unitSize * 10, unitSize * 10));
        setSize(new Dimension(unitSize * 10, unitSize * 10));
        setMaximumSize(new Dimension(unitSize * 10, unitSize * 10));


        //a grid that holds the tiles
        JPanel gridHolder = new JPanel();
        gridHolder.setBounds(0,0,getWidth(),getHeight());
        gridHolder.setLayout(new GridLayout(10,10));
        add(gridHolder);
        this.setFocusable(true);



        for(int y =0; y< 10;y++){
            for(int x =0; x< 10;x++){

                final BattleAnimationPanel panel = new BattleAnimationPanel(unitSize,unitSize);
                panel.setBackground(bgs[2] , 0);
                tiles[x][y] = panel;
                //save the position of the panel
                panel.position = new Point(x,y);

                gridHolder.add(panel);
            }
            repaint();
        }



        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        /*System.out.println("drawing");
        g.setColor(Color.black);
        g.drawRect(0,0,getWidth(),getHeight());
        for(int x =0;x<10;x++){
            g.drawLine(0,x*unitSize , getWidth(),x*unitSize);
        }
        for(int x =0;x<10;x++){
            g.drawLine(x*unitSize,0 ,x*unitSize,getHeight());
        }
        g.drawOval(0,0,30,30);*/
    }
}
