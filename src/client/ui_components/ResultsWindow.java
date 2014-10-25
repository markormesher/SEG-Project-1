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

        //create the font
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

        //3 columns wide grid
        innerGrid = new JPanel(new GridLayout(0,3,5,5));
        innerGrid.setBackground(Color.white);

        //your names
        JLabel yourName = new JLabel(result.username);
        yourName.setFont(font.deriveFont(20f));
        yourName.setHorizontalAlignment(JLabel.HORIZONTAL);
        innerGrid.add(yourName);
        //needed for the empty middle cell in the first row
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
        //percentages rounded up
        addRow(String.valueOf(Math.round((double) result.hits / result.totalShots * 100)) + "%" ,
                "HIT %" , String.valueOf(Math.round((double)opponentResult.hits / opponentResult.totalShots*100)) + "%");

        add(innerGrid , BorderLayout.CENTER);

        JButton play = new JButton("Play again");
        play.setFont(font.deriveFont(25f));
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BattleClientGui().setVisible(true);
                dispose();
            }
        });
        add(play ,BorderLayout.SOUTH);
    }

    /**
     * Adds a new row
     * @param left the text on the first column
     * @param middle left the text on the second column
     * @param right left the text on the third column
     */
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

    //for testing purposes
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

}
