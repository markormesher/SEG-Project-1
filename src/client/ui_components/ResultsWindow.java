package client.ui_components;

import client.BattleClientGui;
import global.Settings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


public class ResultsWindow extends JFrame {

    JPanel innerGrid;
    Font font;
    public ResultsWindow(Result result , Result opponentResult){

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/PressStart2P.ttf"));
            font = font.deriveFont(25f);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        setSize(350, 350);
        setResizable(false);
        getContentPane().setBackground(Color.white);

        setLayout(new BorderLayout());

        JLabel resultLabel = new JLabel(result.won ? "YOU WON" : "YOU LOST");
        resultLabel.setForeground(result.won ? Color.GREEN  : Color.RED);
        resultLabel.setFont(font.deriveFont(35f));
        resultLabel.setBorder(new EmptyBorder(new Insets(12,12,12,12)));
        resultLabel.setHorizontalAlignment(JLabel.HORIZONTAL);
        add(resultLabel, BorderLayout.NORTH);

        innerGrid = new JPanel(new GridLayout(0,3,5,5));
        innerGrid.setBackground(Color.white);
        JLabel yourName = new JLabel(result.username);
        yourName.setFont(font.deriveFont(20f));
        yourName.setHorizontalAlignment(JLabel.HORIZONTAL);
        innerGrid.add(yourName);
        JPanel emptyPanel = new JPanel();
        emptyPanel.setOpaque(false);
        innerGrid.add(emptyPanel);

        JLabel opponentName = new JLabel(opponentResult.username);
        opponentName.setFont(font.deriveFont(20f));
        opponentName.setHorizontalAlignment(JLabel.HORIZONTAL);
        innerGrid.add(opponentName);

        addRow(String.valueOf(result.totalShots) , "SHOTS" , String.valueOf(opponentResult.totalShots));
        addRow(String.valueOf(result.misses) , "MISSES" , String.valueOf(opponentResult.misses));
        addRow(String.valueOf(result.hits) , "HITS" , String.valueOf(opponentResult.hits));
        addRow(String.valueOf(Math.round((double) result.hits / result.totalShots * 100)) + "%" ,
                "HIT %" , String.valueOf(Math.round((double)opponentResult.hits / opponentResult.totalShots*100)) + "%");

        add(innerGrid , BorderLayout.CENTER);
        JButton play = new JButton("Play again");
        play.setFont(font.deriveFont(25f));
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BattleClientGui().setVisible(true);
            }
        });
        add(play ,BorderLayout.SOUTH);
    }

    public void addRow(String left , String middle , String right){

        JLabel myTotal = new JLabel(left);
        myTotal.setFont(font.deriveFont(15f));
        myTotal.setHorizontalAlignment(JLabel.HORIZONTAL);
        innerGrid.add(myTotal);
        JLabel totalLabel = new JLabel(middle);
        totalLabel.setFont(font.deriveFont(13f));
        totalLabel.setHorizontalAlignment(JLabel.HORIZONTAL);
        innerGrid.add(totalLabel);
        JLabel opponentTotal = new JLabel(right);
        opponentTotal.setFont(font.deriveFont(15f));
        opponentTotal.setHorizontalAlignment(JLabel.HORIZONTAL);
        innerGrid.add(opponentTotal);
    }

    public static void main(String[] arr){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception useDefault) {
                    // use default layout
                }
                new ResultsWindow(new Result("JAKE",21,5,17,true), new Result("AMIR",20,8,12,false)).setVisible(true);
            }
        });

    }

    @Override
    public void paintComponents(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0,0,getWidth(),getHeight());
        super.paintComponents(g);
    }

}
