package client;

import global.Settings;
import sun.invoke.empty.Empty;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class AuthLayer extends JPanel{
    Font font;
    // the background tile
    private BufferedImage backgroundTile;
    public AuthLayer(int w , int h){
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/PressStart2P.ttf"));
            font = font.deriveFont(25f);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //setBackground(Color.red);
        setSize(w, h);
        repaint();


        JPanel background = new JPanel();
        //background.setBackground(Color.red);
        background.setSize(new Dimension(300,300));
        background.setPreferredSize(new Dimension(300, 300));
        background.setOpaque(false);
        add(background);

        //setBackground(new Color(30, 144, 255));

        background.setLayout(new BorderLayout());

        EmptyBorder border = new EmptyBorder(12,12,12,12);

        /*JLabel label = new JLabel("LOGIN");
        label.setFont(font.deriveFont(30f));
        label.setHorizontalAlignment(JLabel.CENTER);
        background.add(label,BorderLayout.NORTH);
        label.setBorder(border);*/


        JPanel inner = new JPanel();
        inner.setOpaque(false);
        //inner.setLayout();

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new GridLayout(4,0));
        center.setBorder(border);
        center.setMinimumSize(new Dimension(40*4,0));

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(font.deriveFont(15f));
        usernameLabel.setForeground(Color.white);
        usernameLabel.setHorizontalAlignment(JLabel.CENTER);
        center.add(usernameLabel);
        usernameLabel.setBorder(border);

        final JTextField usernameField = new JTextField();
        usernameField.setFont(font.deriveFont(15f));
        usernameField.setPreferredSize(new Dimension(300, 40));
        usernameField.setSize(new Dimension(300, 40));
        usernameField.setBorder(new CompoundBorder(new EmptyBorder(0,12,0,12) ,  new LineBorder(Color.white , 2)));
        center.add(usernameField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(font.deriveFont(15f));
        passwordLabel.setForeground(Color.white);
        passwordLabel.setHorizontalAlignment(JLabel.CENTER);
        center.add(passwordLabel);
        passwordLabel.setBorder(border);

        final JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(font.deriveFont(15f));
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setSize(new Dimension(300, 40));
        passwordField.setBorder(new CompoundBorder(new EmptyBorder(0, 12, 0, 12), new LineBorder(Color.white, 2)));
        center.add(passwordField);

        inner.add(center);
        background.add(inner,BorderLayout.CENTER);



        final JLabel loginButton = new JLabel("LOGIN");
        loginButton.setForeground(Color.white);
        loginButton.setFont(font.deriveFont(24f));
        loginButton.setHorizontalAlignment(JLabel.CENTER);
        inner.add(loginButton,BorderLayout.SOUTH);
        loginButton.setBorder(border);
        MouseAdapter loginAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if((usernameField.getText().equals("jake") && String.valueOf(passwordField.getPassword()).equals("123"))
                        || (usernameField.getText().equals("amir") && String.valueOf(passwordField.getPassword()).equals("123"))){
                    for(AuthAdapter a : authListeners) a.onLogin(usernameField.getText());
                }
                else{
                    usernameField.setForeground(Color.red);
                    passwordField.setForeground(Color.red);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                loginButton.setForeground(Color.GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                loginButton.setForeground(Color.white);

            }
        };
        loginButton.addMouseListener(loginAdapter);
        loginButton.addMouseMotionListener(loginAdapter);

    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        if (backgroundTile == null) {
            try {
                backgroundTile = ImageIO.read(this.getClass().getResource("/images/default/bg.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // cover the surface with background tiles
        for (int x = 0; x < getWidth() / 32.0; x++) {
            for (int y = 0; y < getHeight() / 32.0; y++) {
                g.drawImage(backgroundTile, x * Settings.IMAGE_CELL_SIZE, y * Settings.IMAGE_CELL_SIZE, Settings.IMAGE_CELL_SIZE, Settings.IMAGE_CELL_SIZE, null);
            }
        }
    }

    ArrayList<AuthAdapter> authListeners = new ArrayList<AuthAdapter>();
    public void addAuthListenr(AuthAdapter a){
        authListeners.add(a);
    }

    interface AuthAdapter{
        void onLogin(String username);
    }
}
