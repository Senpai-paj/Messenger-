package client.boundary;

import client.controller.ClientController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.Objects;

/**
 * This class opens a window for taking username and user image and starting client controller with those parameters.
 */
public class LoginClient extends JFrame implements ActionListener {
    private final ClientController clientController;
    private JPanel loginClient;
    private File image;
    private JButton chooseImage;
    private JFileChooser fileChooser;
    private JLabel userName;
    private JLabel imageLabel;
    private RoundedTextArea writeName;
    private JButton accept;
    private LoginClient itself;

    /**
     * This is constructor of LoginClient which make main format for window for all object which will be added after.
     * @param controller ClientController needed to send username and user icon when it will be taken from user.
     */
    public LoginClient(ClientController controller) {
        this.clientController = controller;
        setResizable(false);
        setSize(350, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setVisible(true);
        populateFrame();
    }

    /**
     * This method adds all objects on LoginClient frame. It also activates accept button which is used to send
     * parameters to "startClient" method.
     */
    private void populateFrame(){
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose an image");

        loginClient = new JPanel();
        loginClient.setSize(350,500);
        loginClient.setLocation(0,0);
        loginClient.setBackground(new Color(234, 229, 0, 255));
        loginClient.setLayout(null);
        loginClient.setVisible(true);
        add(loginClient);

        userName = new JLabel("Username");
        userName.setFont(new Font("ITALIC", 0,18));
        userName.setSize(120,35);
        userName.setLocation(135,310);
        loginClient.add(userName);

        imageLabel = new JLabel();
        imageLabel.setText("SnapCrap");
        imageLabel.setAlignmentX(JTextField.CENTER);
        imageLabel.setFont(new Font("Tahoma", 0,50));
        imageLabel.setSize(250,250);
        imageLabel.setLocation(50,30);
        loginClient.add(imageLabel);

        writeName = new RoundedTextArea(35);
        writeName.setSize(250,35);
        writeName.setLocation(50,350);
        loginClient.add(writeName);

        chooseImage = new JButton();
        chooseImage.addActionListener(this);
        chooseImage.setSize(100,40);
        chooseImage.setLocation(60,400);
        chooseImage.setText("ADD IMAGE");
        loginClient.add(chooseImage);

        accept = new JButton();
        accept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = writeName.getText();
                if(image != null && username != null && !username.equals("") && !username.equals("PM to all")){
                    ImageIcon profilPicture = new ImageIcon(image.getAbsolutePath());
                    startClient(username,profilPicture);
                }
                else{
                    JOptionPane.showMessageDialog(null, "Please choose an image file or valid name!");
                }
            }
        });
        accept.setSize(100,40);
        accept.setLocation(190,400);
        accept.setText("ACCEPT");
        loginClient.add(accept);

        itself = this;
        repaint();
    }

    /**
     * This method used to make animation with help of which LoginClient window will be closed.
     */
    private void collapseLoginClient() {
        Timer timer = new Timer(2, new ActionListener() {
            int xAxis = 350;
            int yAxis = 500;

            @Override
            public void actionPerformed(ActionEvent e) {
                xAxis -= 10;
                yAxis -= 10;
                loginClient.setSize(xAxis, yAxis);
                itself.setSize(xAxis,yAxis);
                itself.setLocationRelativeTo(null);
                if (xAxis == 0 && yAxis >= 0) {
                    itself.dispose();
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }

    /**
     * This method looks if user added a picture and filled name in text bar. Name not suppose to be "PM to all" as
     * this name is reserved for function in program which is sending messages to all users. After control, if all
     * parameters was added correctly they send to startClient method in ClientController class which is creating GUI.
     * @param username name which was written by user.
     * @param image image which was chosen by user.
     */
    public void startClient(String username, ImageIcon image) {
        System.out.println(username);
        if(Objects.equals(username, "") || image == null || Objects.equals(username, "PM to all") || username == null){
            JOptionPane.showMessageDialog(this, "Enter a valid Image and Name");
        }
        else{
            clientController.startClient(username,image);
            collapseLoginClient();
        }

    }

    /**
     * This method activates "chooseImage" button and saving chose image in JPanel in center of window, image replacing
     * logo of program.
     * @param e the event to be processed
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chooseImage) {
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                image = fileChooser.getSelectedFile();
                imageLabel.setIcon(new ImageIcon(new ImageIcon(image.getAbsolutePath()).getImage().getScaledInstance(250, 250, Image.SCALE_DEFAULT)));
                imageLabel.setText("");
            }

        }
    }

    /**
     * This class used to improve JTextArea to make corners round.
     */
    private class RoundedTextArea extends JTextArea {

        private int radius;

        /**
         * Constructor off class
         * @param radius parameter which is used to adjust "roundness" of JTextArea
         */
        public RoundedTextArea(int radius) {
            this.radius = radius;
            setOpaque(false);
            setLineWrap(true);
            setWrapStyleWord(true);
            setMargin(new Insets(5,5,5,5));
        }

        /**
         * This method is used to make corners round.
         * @param g the <code>Graphics</code> object to protect
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Shape border = new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            g2.setColor(getBackground());
            g2.fill(border);
            g2.setColor(getForeground());
            g2.draw(border);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
