package client.ui_components;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class EmoticonsFrame extends JFrame {

    //codes and shortcuts of emoticons
    private static final String[][] emoticonArray = {
            {"GRIN",":)"},
            {"WINK",";)"},
            {"SMILE",":D"},
            {"CRY",":'("},
            {"SAD",":("},
            {"TONGUE",":P"},
            {"SHOCK",":O"},
            {"ANGRY",">:("},
            {"COOL","B)"}
    };

    private static final int CODE = 0;
    private static final int SHORTCUT = 1;
    private static final int HEIGHT = 46;
    private static final int WIDTH = 46;

    //store all emoticon buttons in list
    private ArrayList<JButton> buttonArrayList;
    private String selectedEmoticon;

    //create all components, set frame to currently open
    public EmoticonsFrame() {
        createComponents();
    }

    //create all components
    private void createComponents() {

        //hide frame title bar and set size
        setUndecorated(true);
        setSize(new Dimension(HEIGHT, WIDTH));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buttonArrayList = new ArrayList<JButton>();

        //create layout and add buttons
        this.setLayout(new GridLayout(3, 3));
        createButtons();
        pack();
    }

    //create all emoticon buttons, add to list and to layout
    private void createButtons() {
        for(int i = 0; i < emoticonArray.length; ++i) {
            try {
                //find and assign new icon image and add to new button
                Image newEmoticon = ImageIO.read(getClass().getResource("/images/emoticons/" + emoticonArray[i][CODE] + ".png"));
                final JButton newEmoticonButton = new JButton();
                newEmoticonButton.setIcon(new ImageIcon(newEmoticon));
                newEmoticonButton.setSize(16, 16);

                /*final String currentEmoticon = emoticonArray[i][SHORTCUT];

                newEmoticonButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        selectedEmoticon = currentEmoticon;
                    }
                });*/

                //add button to array list
                buttonArrayList.add(newEmoticonButton);

                //add button to layout
                this.add(newEmoticonButton);

            } catch (IOException e) {
                //TODO: handle exception
                System.out.println(e.getMessage());
            }
        }
    }
}