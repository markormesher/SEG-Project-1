package client.ui_components;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class EmoticonsFrame extends JFrame {

    //codes and shortcuts of emoticons
    private static final String[][] emoticonArray = {
            {"grin",":)"},
            {"wink",";)"},
            {"smile",":D"},
            {"cry",":'("},
            {"sad",":("},
            {"tongue",":P"},
            {"shock",":O"},
            {"angry",">:("},
            {"cool","B)"}
    };

    private static final int CODE = 0;
    private static final int SHORTCUT = 1;
    private static final int HEIGHT = 46;
    private static final int WIDTH = 46;
    private static final String IMAGE_PATH = "/images/emoticons/";

    //textfield sent from client
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

        //create layout and add buttons
        this.setLayout(new GridLayout(3, 3));
        createButtons();
        pack();

    }

    //create all emoticon buttons, add to list and to layout
    private void createButtons() {
        for(int i = 0; i < emoticonArray.length; ++i) {


                //create new emoticon button
                final JButton newEmoticonButton = new JButton();
            try {
                Image newEmoticon = ImageIO.read(getClass().getResource(IMAGE_PATH + emoticonArray[i][CODE] + ".png"));
                newEmoticonButton.setIcon(new ImageIcon(newEmoticon));
                this.setSize(16, 16);
            } catch (IOException e) {
                //TODO: handle exception
            }

                //temporarily store the text shortcut of this emoticon
                final String currentEmoticonShortcut = emoticonArray[i][SHORTCUT];

                //when button is pressed add text shortcut of emoticon to text field
                newEmoticonButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int caretPosition = chatInput.getCaretPosition();

                        //if caret is ahead of text, append emoticon shortcut
                        if (caretPosition == chatInput.getText().length()) {
                            chatInput.setText(chatInput.getText() + currentEmoticonShortcut);
                        }
                        //else insert emoticon shortcut in caret position and set caret ahead of inserted shortcut
                        else {
                            String textBeforeCaret = chatInput.getText().substring(0, caretPosition);
                            String textAfterCaret = chatInput.getText().substring(caretPosition);
                            chatInput.setText(textBeforeCaret + currentEmoticonShortcut + textAfterCaret);
                            chatInput.setCaretPosition(caretPosition + currentEmoticonShortcut.length());
                        }
                    }
                });

                //add button to layout
                this.add(newEmoticonButton);


        }

        pack();
    }

    //check whether emoticons where entered in message
    public boolean containsEmoticons(String msg) {
        for (int i = 0; i < emoticonArray.length; ++i) {
            if (msg.contains(emoticonArray[i][SHORTCUT])) {
                return true;
            }
        }
        return false;
    }

    //convert emoticon shortcuts to images
    public String convertTextToHTML(String msg) {
        String newMessage = "";
        boolean emoticonFound = false;

        //loop through every character
        for (int currentIndex = 0; currentIndex < msg.length(); ++currentIndex) {
            char currentChar = msg.charAt(currentIndex);
            //check if character is the first character of an existing emoticon
            if (currentChar == ':' || currentChar == '>' || currentChar == 'B' || currentChar == ';') {
                for (int i = 0; i < emoticonArray.length; ++i) {
                    //if the following character completes a 2 char emoticon create image link
                    if (msg.length() - currentIndex > 1 && msg.substring(currentIndex, currentIndex + 2).equals(emoticonArray[i][SHORTCUT])) {
                        newMessage = newMessage + "<img src=\"" + emoticonArray[i][CODE] + ".png\"/>";
                        ++currentIndex;
                        emoticonFound = true;
                        break;
                    }
                    //if the following 2 characters complete a 3 char emoticon create image link
                    else if (msg.length() - currentIndex > 2 && msg.substring(currentIndex, currentIndex + 3).equals(emoticonArray[i][SHORTCUT])) {
                        newMessage = newMessage + "<img src=\"" + emoticonArray[i][CODE] + ".png\"/>";
                        currentIndex += 2;
                        emoticonFound = true;
                        break;
                    }
                }
            }
            //if emoticon was found reset boolean
            if(emoticonFound) {
                emoticonFound = false;
            }
            //if emoticon was not found add the char as it is
            else {
                newMessage = newMessage + currentChar;
            }
        }

        return newMessage;
    }

}