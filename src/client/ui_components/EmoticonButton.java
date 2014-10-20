package client.ui_components;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class EmoticonButton extends JButton {

    //create a new emoticon button
    public EmoticonButton(String imagePath) {
        try {
            Image newEmoticon = ImageIO.read(getClass().getResource(imagePath));
            this.setIcon(new ImageIcon(newEmoticon));
            this.setSize(16, 16);
        } catch (IOException e) {
            //TODO: handle exception
        }
    }
}
