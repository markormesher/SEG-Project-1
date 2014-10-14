package client.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;


public class BoardDemo extends JFrame {
    public BoardDemo(){
        super("Board demo");
        setSize(new Dimension(320 , 320));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //setLayout(new FlowLayout());
       // JLayeredPane pane = new JLayeredPane();
       // pane.setBackground(Color.BLUE);
       // add(pane);
        int unitSize = 32;
        final BattleBoard board = new BattleBoard();
        getLayeredPane().add(board, new Integer(1));

        final JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setSize(320, 320);
        final BattleAnimationPanel front = new BattleAnimationPanel(unitSize,unitSize);
        front.setAsShipEnd(BattleAnimationPanel.NORTH);
        final BattleAnimationPanel middle = new BattleAnimationPanel(unitSize,unitSize);
        middle.setAsShipMiddle(BattleAnimationPanel.VERTICAL);
        final BattleAnimationPanel bottom = new BattleAnimationPanel(unitSize,unitSize);
        bottom.setAsShipEnd(BattleAnimationPanel.SOUTH);
        panel.add(front);
        panel.add(middle);
        panel.add(bottom);
        panel.setVisible(false);

        board.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                panel.setVisible(true);

                front.setBounds(e.getX()-16, e.getY() -8, 32, 32);
                middle.setBounds(e.getX()-16, e.getY()-8 + 32, 32, 32);
                bottom.setBounds(e.getX()-16, e.getY()-8 + 32 + 32, 32, 32);


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
                System.out.println("click");
                int x = (int)Math.floor( e.getX() /32.0);
                int y = (int)Math.floor( e.getY() /32.0);
                if(x >7 || y > 7) return;
                board.tiles[x][y].setAsShipEnd(BattleAnimationPanel.NORTH);
                board.tiles[x][y+1].setAsShipMiddle(BattleAnimationPanel.VERTICAL);
                board.tiles[x][y+2].setAsShipEnd(BattleAnimationPanel.SOUTH);
            }
        });

        getLayeredPane().add(panel,new Integer(3));
    }

    public static void main(String[] arr){
        BoardDemo demo = new BoardDemo();
        demo.setVisible(true);
    }
}
