package client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * This is the board for the current player
 */
public class BattleBoardLocal extends JPanel {
    //the shapes of each ship in order
    int[] shipSizes = {2,3,3,4,5};
    //the current ship position in the array
    int currentSize = 0;
    //is the user placing stuff horizontally?
    boolean horizontal = false;
    //inner board with the tiles
    final BattleBoard board = new BattleBoard();

    public BattleBoardLocal(){


        setLayout(null);
        setBackground(Color.BLUE);
        final JLayeredPane layeredPane = new JLayeredPane();

        add(layeredPane);
        layeredPane.setBounds(0,0,360,360);
         setPreferredSize(new Dimension(360, 360));
         setSize(new Dimension(360,360));
         //layeredPane.setPreferredSize(new Dimension(360, 360));
        //layeredPane.setSize(new Dimension(360,360));

            final int unitSize = 36;


             layeredPane.add(board, new Integer(2));

            //this panel is overlayed on the board for the ship moving
            final JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setPreferredSize(new Dimension(360, 360));
        panel.setSize(new Dimension(360, 360));
        //panel.setBackground(Color.red);





            //the ship the user can move
            final JPanel shipContainer = new JPanel();
            shipContainer.setLayout(new BoxLayout(shipContainer,BoxLayout.Y_AXIS));

            final BattleAnimationPanel front = new BattleAnimationPanel(unitSize,unitSize);


            //the front and back parts of the ship are always there so they can be used stright away
            final BattleAnimationPanel bottom = new BattleAnimationPanel(unitSize,unitSize);
            bottom.setAsShipEnd(BattleAnimationPanel.SOUTH);
            front.setAsShipEnd(BattleAnimationPanel.NORTH);
            shipContainer.add(front);
            //shipContainer.add(middle);
            shipContainer.add(bottom);
            panel.add(shipContainer);
            shipContainer.setOpaque(false);
            panel.setVisible(false);


            setFocusable(true);




        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                panel.setVisible(true);


                //these are the coordonates of the ship the user moves
                int x1 = (horizontal)?e.getX()-18: e.getX()-18;
                int y1 = (horizontal)?e.getY()-9:e.getY()-9;


                shipContainer.setBounds(x1,y1,
                        horizontal? shipContainer.getComponentCount() * 36 : 36 ,
                        horizontal? 36: shipContainer.getComponentCount() * 36);

                layeredPane.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //on right click , switch orientation
                if(SwingUtilities.isRightMouseButton(e)){

                        horizontal = !horizontal;
                        //shipContainer.setOpaque(true);
                        //shipContainer.setBackground(Color.red);
                        shipContainer.setLayout(new BoxLayout(shipContainer, horizontal ? BoxLayout.X_AXIS : BoxLayout.Y_AXIS));
                        shipContainer.setBounds(shipContainer.getX(), shipContainer.getY(),
                                horizontal ? shipContainer.getComponentCount() * 36 : 36,
                                horizontal ? 36 : shipContainer.getComponentCount() * 36);
                        front.setAsShipEnd(!horizontal? BattleAnimationPanel.NORTH : BattleAnimationPanel.WEST);
                        for(Component c : shipContainer.getComponents()){
                            if(c!= front && c!= bottom) ((BattleAnimationPanel)c).setAsShipMiddle(horizontal?BattleAnimationPanel.HORIZONTAL:BattleAnimationPanel.VERTICAL);
                        }
                        bottom.setAsShipEnd(!horizontal? BattleAnimationPanel.SOUTH : BattleAnimationPanel.EAST);
                        shipContainer.doLayout();

                }else {

                    int x = (int) Math.floor(e.getX() / 36.0);
                    int y = (int) Math.floor(e.getY() / 36.0);
                    //prevent overflows
                    if ((x > 7 && horizontal) || (y > 7 && !horizontal)) return;

                    //find all tiles that need to be updated
                    int x1 = x + (horizontal ? 1 : 0);
                    int x2 = x + (horizontal ? 2 : 0);
                    int y1 = y + (!horizontal ? 1 : 0);
                    int y2 = y + (!horizontal ? 2 : 0);

                    //if any of them have an object , stop
                    if (board.tiles[x][y].object.getIcon() != null ||
                            board.tiles[x1][y1].object.getIcon() != null ||
                            board.tiles[x2][y2].object.getIcon() != null) {
                        return;
                    }
                    int current = shipSizes[currentSize];

                    //set the actual tiles on the board
                    board.tiles[x][y].setAsShipEnd(horizontal ? BattleAnimationPanel.WEST : BattleAnimationPanel.NORTH);
                    for (int r = 0; r < current - 2; r++) {
                        board.tiles[x + (r + 1) * (!horizontal ? 0 : 1)][y + (r + 1) * (horizontal ? 0 : 1)].setAsShipMiddle(horizontal ? BattleAnimationPanel.HORIZONTAL : BattleAnimationPanel.VERTICAL);
                    }
                    //board.tiles[x1][y1].setAsShipMiddle( horizontal ? BattleAnimationPanel.HORIZONTAL : BattleAnimationPanel.VERTICAL);
                    board.tiles[x + (current - 2 + 1) * (!horizontal ? 0 : 1)][y + (current - 2 + 1) * (horizontal ? 0 : 1)].setAsShipEnd(horizontal ? BattleAnimationPanel.EAST : BattleAnimationPanel.SOUTH);

                    currentSize++;
                    if (currentSize > 4) {
                        shipContainer.removeAll();
                        for(ShipPlacementListener listener:listeners)listener.onFinished();
                        return;
                    }
                    current = shipSizes[currentSize];
                    shipContainer.remove(bottom);
                    for (Component c : shipContainer.getComponents()) {
                        if (c != front) shipContainer.remove(c);
                    }
                    for (int r = 0; r < current - 2; r++) {
                        BattleAnimationPanel panel = new BattleAnimationPanel(unitSize, unitSize);
                        panel.setAsShipMiddle(horizontal ? BattleAnimationPanel.HORIZONTAL : BattleAnimationPanel.VERTICAL);
                        shipContainer.add(panel);
                    }
                    shipContainer.add(bottom);
                }
            }
        };
            board.addMouseListener(mouseAdapter);

        board.addMouseMotionListener(mouseAdapter);

            //the panel is at a higher level than the board.
        layeredPane.add(panel, new Integer(3));
    }

    public BattleAnimationPanel[][] getTiles(){
        return board.tiles;
    }

    //when all pieces are placed this will be triggered
    ArrayList<ShipPlacementListener> listeners = new ArrayList<ShipPlacementListener>();

    public void addShipPlacementListener(ShipPlacementListener listener){
        listeners.add(listener);
    }


}
