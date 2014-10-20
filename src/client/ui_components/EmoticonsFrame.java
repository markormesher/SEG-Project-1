package client.ui_components;

import client.BattleClientGui;

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

    private JTextField chatInput;

    //create all components, set frame to currently open
    public EmoticonsFrame(JTextField chatInput) {
        this.chatInput = chatInput;
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


                //create new emoticon button
                final EmoticonButton newEmoticonButton = new EmoticonButton("/images/emoticons/" + emoticonArray[i][CODE] + ".png");

                //temporarily store the text shortcut of this emoticon
                final String currentEmoticonShortcut = emoticonArray[i][SHORTCUT];

                //when button is pressed add text shortcut of emoticon to text field
                newEmoticonButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //TODO: make text enter where the carot is in text field
                        chatInput.setText(chatInput.getText() + currentEmoticonShortcut);
                    }
                });

                //add button to array list
                buttonArrayList.add(newEmoticonButton);

                //add button to layout
                this.add(newEmoticonButton);


        }

        pack();
    }


}