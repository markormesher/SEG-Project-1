package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * This is the opponent's board
 */
public class BattleBoardOpponent extends JPanel {
    final BattleBoard board = new BattleBoard();

    public BattleBoardOpponent(){


        setLayout(null);
        setBackground(Color.BLUE);
        final JLayeredPane layeredPane = new JLayeredPane();

        add(layeredPane);
        layeredPane.setBounds(0,0,360,360);
        setPreferredSize(new Dimension(360, 360));
        setSize(new Dimension(360,360));
        //layeredPane.setPreferredSize(new Dimension(360, 360));
        //layeredPane.setSize(new Dimension(360,360));


        layeredPane.add(board, new Integer(2));

        //this panel is overlayed on the board for the ship moving
        final JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(360, 360));
        panel.setSize(new Dimension(360, 360));

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                panel.setVisible(true);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //find the tile and trigger the event
                int x = (int) Math.floor(e.getX() / 36.0);
                int y = (int) Math.floor(e.getY() / 36.0);
                for(ShootingListener listener:listeners) listener.onShooting(x,y);
            }
        };
        board.addMouseListener(mouseAdapter);

       // board.addMouseMotionListener(mouseAdapter);

        //the panel is at a higher level than the board.
        layeredPane.add(panel, new Integer(3));
    }

    interface ShootingListener{
        void onShooting(int x , int y);
    }
    public BattleAnimationPanel[][] getTiles(){
        return board.tiles;
    }

    ArrayList<ShootingListener> listeners = new ArrayList<ShootingListener>();

    public void addShipPlacementListener(ShootingListener listener){
        listeners.add(listener);
    }

}
