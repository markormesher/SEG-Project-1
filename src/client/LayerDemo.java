package client;

import javax.swing.*;
import java.awt.*;

/**
 * Used to test one or more layers without needing the client gui
 */
public class LayerDemo extends JFrame {
    public static void main(String[] arr){
        LayerDemo demo = new LayerDemo();
        demo.setSize(600,600);
        //demo.getLayeredPane().setLayout(new BorderLayout());
        demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        demo.getLayeredPane().add(new AuthLayer(600,600) , 0);
        demo.setVisible(true);
    }
}
