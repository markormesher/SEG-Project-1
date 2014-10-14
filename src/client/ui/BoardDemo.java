package client.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;


public class BoardDemo extends JFrame {
    //should ships be placed horizontally
    int[] shipSizes = {2,3,3,4,5};
    int currentSize = 0;
    boolean horizontal = false;
    public BoardDemo(){
        super("Board demo");
        setSize(new Dimension(320 , 320));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        final int unitSize = 32;
        final BattleBoard board = new BattleBoard();
        getLayeredPane().add(board, new Integer(1));

        //this panel is overlayed on the board for the ship moving
        final JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setSize(320, 320);





        //the ship the user can move

        final JPanel shipContainer = new JPanel();
        shipContainer.setLayout(new BoxLayout(shipContainer,BoxLayout.Y_AXIS));

        final BattleAnimationPanel front = new BattleAnimationPanel(unitSize,unitSize);

        //final BattleAnimationPanel middle = new BattleAnimationPanel(unitSize,unitSize);

        final BattleAnimationPanel bottom = new BattleAnimationPanel(unitSize,unitSize);
        bottom.setAsShipEnd(BattleAnimationPanel.SOUTH);
        front.setAsShipEnd(BattleAnimationPanel.NORTH);
        //middle.setAsShipMiddle(BattleAnimationPanel.VERTICAL);
        shipContainer.add(front);
        //shipContainer.add(middle);
        shipContainer.add(bottom);
        panel.add(shipContainer);
        shipContainer.setOpaque(false);
        panel.setVisible(false);

        setFocusable(true);

        //for the demo , press r to switch orientation , move to see effect
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyChar() == 'r') {
                    System.out.println("key");
                    horizontal = !horizontal;
                    //shipContainer.setOpaque(true);
                    //shipContainer.setBackground(Color.red);
                    shipContainer.setLayout(new BoxLayout(shipContainer, horizontal ? BoxLayout.X_AXIS : BoxLayout.Y_AXIS));
                    shipContainer.setBounds(shipContainer.getX(), shipContainer.getY(),
                            horizontal ? shipContainer.getComponentCount() * 32 : 32,
                            horizontal ? 32 : shipContainer.getComponentCount() * 32);
                    front.setAsShipEnd(!horizontal? BattleAnimationPanel.NORTH : BattleAnimationPanel.WEST);
                    for(Component c : shipContainer.getComponents()){
                        if(c!= front && c!= bottom) ((BattleAnimationPanel)c).setAsShipMiddle(horizontal?BattleAnimationPanel.HORIZONTAL:BattleAnimationPanel.VERTICAL);
                    }
                    bottom.setAsShipEnd(!horizontal? BattleAnimationPanel.SOUTH : BattleAnimationPanel.EAST);
                    shipContainer.doLayout();
                }
            }
        });

        board.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                panel.setVisible(true);


                //these are the coordonates of the ship the user moves
                int x1 = (horizontal)?e.getX()-16: e.getX()-16;
                int x2 = (horizontal)?e.getX()+16 :x1;
                int x3 = (horizontal)?e.getX()+16+32:x1;

                int y1 = (horizontal)?e.getY()-8:e.getY()-8;
                /*int y2 = (horizontal)?y1 : e.getY()-8+32;
                int y3 = (horizontal)?y1:e.getY()-8+32+32;
                front.setBounds(x1,y1, 32, 32);
                middle.setBounds(x2,y2 , 32, 32);
                bottom.setBounds(x3,y3, 32, 32);

                front.setAsShipEnd(horizontal ? BattleAnimationPanel.WEST : BattleAnimationPanel.NORTH);
                middle.setAsShipMiddle( horizontal ? BattleAnimationPanel.HORIZONTAL : BattleAnimationPanel.VERTICAL);
                bottom.setAsShipEnd(horizontal ? BattleAnimationPanel.EAST : BattleAnimationPanel.SOUTH);*/

                shipContainer.setBounds(x1,y1,
                        horizontal? shipContainer.getComponentCount() * 32 : 32 ,
                        horizontal? 32: shipContainer.getComponentCount() * 32);

                //this was supposed to highlight the squares that are selected
                //but swing is ignoring the event when i do :|
                //int x = (int)Math.floor( e.getX() / 32.0);
                //int y = (int)Math.floor( e.getY() / 32.0);
                //if(x > 6 || y > 6) return;
                //System.out.println(x + " " + y);
                //final LineBorder selectedBorder = new LineBorder(Color.yellow , 1);
                //for(int rx = 0; rx<10;rx++)for(int ry = 0; ry<10;ry++) board.tiles[rx][ry].background.setVisible(true);

                //board.tiles[x][y].setBackground(Color.GREEN);
                //board.tiles[x][y].background.setVisible(false);
                //board.tiles[x][y+1].setBorder(selectedBorder);
                //board.tiles[x][y+2].setBorder(selectedBorder);
                getLayeredPane().repaint();
            }


        });




        board.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //find the tile pressed
                int x = (int)Math.floor( e.getX() /32.0);
                int y = (int)Math.floor( e.getY() /32.0);
                //prevent overflows
                if( (x >7 && horizontal) || (y > 7 && !horizontal)) return;

                //find all tiles that need to be updated
                int x1 = x+ (horizontal?1:0);
                int x2 = x+(horizontal?2:0);
                int y1 = y+(!horizontal?1:0);
                int y2 = y+(!horizontal?2:0);

                //if any of them have an object , stop
                if(board.tiles[x][y].object.getIcon() != null ||
                   board.tiles[x1][y1].object.getIcon() !=null||
                   board.tiles[x2][y2].object.getIcon() !=null){
                    return;
                }
                int current = shipSizes[currentSize];

                //set the actual tiles on the board
                board.tiles[x][y].setAsShipEnd(horizontal ? BattleAnimationPanel.WEST : BattleAnimationPanel.NORTH);
                for(int r =0;r<current-2;r++){
                    board.tiles[x+ (r+1) * (!horizontal?0:1)][y + (r+1) * (horizontal?0:1)].setAsShipMiddle(horizontal ? BattleAnimationPanel.HORIZONTAL : BattleAnimationPanel.VERTICAL);
                }
                //board.tiles[x1][y1].setAsShipMiddle( horizontal ? BattleAnimationPanel.HORIZONTAL : BattleAnimationPanel.VERTICAL);
                board.tiles[x+ (current-2+1) * (!horizontal?0:1)][y + (current-2+1) * (horizontal?0:1)].setAsShipEnd(horizontal ? BattleAnimationPanel.EAST : BattleAnimationPanel.SOUTH);

                currentSize++;
                if(currentSize > 4){
                    shipContainer.removeAll();
                    return;
                }
                current = shipSizes[currentSize];
                shipContainer.remove(bottom);
                for(Component c: shipContainer.getComponents()){
                    if(c != front) shipContainer.remove(c);
                }
                for(int r = 0; r < current-2;r++) {
                    BattleAnimationPanel panel = new BattleAnimationPanel(unitSize,unitSize);
                    panel.setAsShipMiddle(horizontal ? BattleAnimationPanel.HORIZONTAL : BattleAnimationPanel.VERTICAL);
                    shipContainer.add(panel);
                }
                shipContainer.add(bottom);
            }
        });

        //the panel is at a higher level than the board.
        getLayeredPane().add(panel,new Integer(3));
    }

    public static void main(String[] arr){
        BoardDemo demo = new BoardDemo();
        demo.setVisible(true);
    }
}
