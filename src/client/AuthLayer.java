package client;

import global.Settings;
import sun.invoke.empty.Empty;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
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

        //load the font
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

        //this is used to center the login and signups vertically
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints ();

        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;


        //2 collumns , login on left and signup on right
        JPanel gridHolder = new JPanel(new GridLayout(0,2));
        gridHolder.setPreferredSize(new Dimension(600,350));
        gridHolder.setOpaque(false);
        add(gridHolder, gbc);




        //the first collumn , for signup
        JPanel background = new JPanel();
        background.setOpaque(false);
        background.setSize(new Dimension(300,300));
        background.setPreferredSize(new Dimension(300, 300));

        gridHolder.add(background);

        background.setLayout(new BorderLayout());

        EmptyBorder border = new EmptyBorder(12,12,12,12);


        JPanel inner = new JPanel();
        inner.setOpaque(false);

        //4 rows : username label + field , password label+field
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
        usernameField.setPreferredSize(new Dimension(280, 40));
        usernameField.setSize(new Dimension(280, 40));
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
                //test auths , jake : 123 , amir : 123
                if((usernameField.getText().equals("jake") && String.valueOf(passwordField.getPassword()).equals("123"))
                        || (usernameField.getText().equals("amir") && String.valueOf(passwordField.getPassword()).equals("123"))){
                    for(AuthAdapter a : authListeners) a.onLogin(usernameField.getText());
                }
                else{
                    usernameField.setForeground(Color.red);
                    passwordField.setForeground(Color.red);
                }
            }

            //for hovering effect
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


        //pretty much the same as above but for the signup panel on the right

        JPanel backgroundSignup = new JPanel();
        backgroundSignup.setOpaque(false);
        backgroundSignup.setSize(new Dimension(270,300));
        backgroundSignup.setPreferredSize(new Dimension(300, 300));
        //background.setOpaque(false);

        gridHolder.add(backgroundSignup);


        backgroundSignup.setLayout(new BorderLayout());




        JPanel innerSignup = new JPanel();
        innerSignup.setOpaque(false);

        JPanel centerSignup = new JPanel();
        centerSignup.setOpaque(false);
        centerSignup.setLayout(new GridLayout(6,0));
        centerSignup.setBorder(border);
        centerSignup.setMinimumSize(new Dimension(40*4,0));

        JLabel usernameLabelSignup = new JLabel("Username");
        usernameLabelSignup.setFont(font.deriveFont(15f));
        usernameLabelSignup.setForeground(Color.white);
        usernameLabelSignup.setHorizontalAlignment(JLabel.CENTER);
        centerSignup.add(usernameLabelSignup);
        usernameLabelSignup.setBorder(border);

        final JTextField usernameFieldSignup = new JTextField();
        usernameFieldSignup.setFont(font.deriveFont(15f));
        usernameFieldSignup.setPreferredSize(new Dimension(280, 40));
        usernameFieldSignup.setSize(new Dimension(280, 40));
        usernameFieldSignup.setBorder(new CompoundBorder(new EmptyBorder(0,12,0,12) ,  new LineBorder(Color.white , 2)));
        centerSignup.add(usernameFieldSignup);

        JLabel passwordLabelSignup = new JLabel("Password");
        passwordLabelSignup.setFont(font.deriveFont(15f));
        passwordLabelSignup.setForeground(Color.white);
        passwordLabelSignup.setHorizontalAlignment(JLabel.CENTER);
        centerSignup.add(passwordLabelSignup);
        passwordLabelSignup.setBorder(border);

        final JPasswordField passwordFieldSignup = new JPasswordField();
        passwordFieldSignup.setFont(font.deriveFont(15f));
        passwordFieldSignup.setPreferredSize(new Dimension(280, 40));
        passwordFieldSignup.setSize(new Dimension(280, 40));
        passwordFieldSignup.setBorder(new CompoundBorder(new EmptyBorder(0, 12, 0, 12), new LineBorder(Color.white, 2)));
        centerSignup.add(passwordFieldSignup);

        JLabel passwordLabelSignup2 = new JLabel("Password");
        passwordLabelSignup2.setFont(font.deriveFont(15f));
        passwordLabelSignup2.setForeground(Color.white);
        passwordLabelSignup2.setHorizontalAlignment(JLabel.CENTER);
        centerSignup.add(passwordLabelSignup2);
        passwordLabelSignup.setBorder(border);

        final JPasswordField passwordFieldSignup2 = new JPasswordField();
        passwordFieldSignup2.setFont(font.deriveFont(15f));
        passwordFieldSignup2.setPreferredSize(new Dimension(280, 40));
        passwordFieldSignup2.setSize(new Dimension(280, 40));
        passwordFieldSignup2.setBorder(new CompoundBorder(new EmptyBorder(0, 12, 0, 12), new LineBorder(Color.white, 2)));
        centerSignup.add(passwordFieldSignup2);

        innerSignup.add(centerSignup);
        backgroundSignup.add(innerSignup,BorderLayout.CENTER);



        final JLabel loginButtonSignup = new JLabel("SIGN UP");
        loginButtonSignup.setForeground(Color.white);
        loginButtonSignup.setFont(font.deriveFont(24f));
        loginButtonSignup.setHorizontalAlignment(JLabel.CENTER);
        innerSignup.add(loginButtonSignup,BorderLayout.SOUTH);
        loginButtonSignup.setBorder(border);
        MouseAdapter loginAdapterSignup = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                loginButtonSignup.setForeground(Color.GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                loginButtonSignup.setForeground(Color.white);

            }
        };
        loginButtonSignup.addMouseListener(loginAdapterSignup);
        loginButtonSignup.addMouseMotionListener(loginAdapterSignup);


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
    public void addAuthListener(AuthAdapter a){
        authListeners.add(a);
    }

    interface AuthAdapter{
        void onLogin(String username);
    }
}
