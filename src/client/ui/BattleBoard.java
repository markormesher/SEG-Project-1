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

    public BattleAnimationPanel[][] tiles = new BattleAnimationPanel[10][10];

    boolean isHorizontal = false;
    int c = 0;
    public BattleBoard(){
        setPreferredSize(new Dimension(unitSize * 10, unitSize * 10));
        setSize(new Dimension(unitSize * 10, unitSize * 10));
        setMaximumSize(new Dimension(unitSize * 10, unitSize * 10));


        JPanel gridHolder = new JPanel();
        gridHolder.setBounds(0,0,getWidth(),getHeight());
        gridHolder.setLayout(new GridLayout(10,10));
        add(gridHolder);
        this.setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyChar() == 'r') {
                    isHorizontal = !isHorizontal;
                }
            }
        });

        for(int y =0; y< 10;y++){
            for(int x =0; x< 10;x++){
                final BattleAnimationPanel panel = new BattleAnimationPanel(unitSize,unitSize);
                panel.setBackground(bgs[2] , 0);
                tiles[x][y] = panel;
                panel.position = new Point(x,y);
                /*panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        super.mouseEntered(e);
                        panel.setBorder(selectedBorder);
                        tiles[panel.position.x][panel.position.y+1].setBorder(selectedBorder);
                        tiles[panel.position.x][panel.position.y+2].setBorder(selectedBorder);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        super.mouseExited(e);
                        panel.setBorder(null);
                        tiles[panel.position.x][panel.position.y+1].setBorder(null);
                        tiles[panel.position.x][panel.position.y+2].setBorder(null);
                    }
                });
                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        c++;
                        super.mouseClicked(e);
                        panel.setObject(objects[c % objects.length],0);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        super.mouseEntered(e);
                        panel.setAsShipEnd(isHorizontal ? BattleAnimationPanel.WEST : BattleAnimationPanel.NORTH);
                        tiles[panel.position.x+ (isHorizontal ? 1 : 0)][panel.position.y + (!isHorizontal ? 1 : 0)].setAsShipMiddle(isHorizontal ? BattleAnimationPanel.HORIZONTAL:BattleAnimationPanel.VERTICAL);
                        tiles[panel.position.x+2 * (isHorizontal?1:0)][panel.position.y + 2 * (!isHorizontal?1:0)].setAsShipEnd( isHorizontal ? BattleAnimationPanel.EAST:BattleAnimationPanel.SOUTH);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        super.mouseExited(e);
                        panel.setBackground("bg2", 0);
                        panel.setObject(null,0);
                        tiles[panel.position.x+1*(isHorizontal?1:0)][panel.position.y+1 *(!isHorizontal?1:0)].setObject(null,0);
                        tiles[panel.position.x+2*(isHorizontal?1:0)][panel.position.y+2 *(!isHorizontal?1:0)].setObject(null, 0);
                    }
                });*/
                gridHolder.add(panel);
            }
            repaint();
        }


        addMouseListener( new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

            }
        });

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
