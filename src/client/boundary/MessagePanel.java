package client.boundary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

/**
 * This class represents chat window in program and provide all funktions which user able to do with interlocutor.
 */
public class MessagePanel extends JPanel implements ActionListener {
    private long currentTimeMillis = System.currentTimeMillis();
    private Date currentDate = new Date(currentTimeMillis);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private String formattedDate = dateFormat.format(currentDate);
    private int yAxis = 0;
    private JButton goBack = new JButton();
    private JButton sendMessage = new JButton();
    private ClientGUI clientGUI;
    private ArrayList<JLabel> imageContainer = new ArrayList<>();
    private JLabel headerLabel;
    private String userName;
    private JScrollPane messageView;
    private JButton send = new JButton();
    private ArrayList<JTextArea> messages = new ArrayList<>();
    private JTextField textField = new JTextField();
    private JFileChooser fileChooser = new JFileChooser();
    private JButton friend = new JButton();
    private JButton picture = new JButton();
    private JPanel messagePanel;
    private File selectedFile;
    private UserPanel userPanel;
    private ImageIcon userIcon;
    private RoundedTextArea popup = new RoundedTextArea(30);

    /**
     * Constructor of class MessagePanel. Fetching all received parameters and creating base form of panel-
     * @param userName Receiver username.
     * @param controller ClientGUI.
     * @param userPanel userPanel of  client.
     * @param userIcon Image of receiver.
     */
    public MessagePanel(String userName, ClientGUI controller, UserPanel userPanel, ImageIcon userIcon) {
        //System.out.println("New chat created with " + userName);
        this.userIcon = userIcon;
        this.userName = userName;
        clientGUI = controller;
        this.userPanel = userPanel;
        setSize(350, 550);
        setLocation(0, 0);
        setLayout(null);
        populatePanel();
    }

    /**
     * This method creates all buttons and panels which needs to be show on message panel, its also activates friend button.
     */
    private void populatePanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setSize(350, 30);
        headerPanel.setLocation(0, 0);
        headerPanel.setBackground(Color.yellow);
        add(headerPanel);

        //friend.addActionListener(this);
        headerPanel.add(friend);

        goBack.addActionListener(this);
        goBack.setText("<");
        goBack.setBackground(Color.yellow);
        goBack.setFont(new Font("ITALIC", Font.BOLD, 26));
        goBack.setBorder(null);
        goBack.setSize(40, 35);
        goBack.setLocation(0, -3);
        headerPanel.add(goBack);


        popup.setSize(100,40);
        popup.setText("Friend added");
        popup.setLocation(350,40);
        popup.setBackground(Color.black);
        popup.setForeground(Color.white);
        messagePanel = new JPanel(null);
        messagePanel.add(popup);

        friend.addActionListener(e -> {
            //System.out.println("in event listener");
            if(userPanel.checkFriend(userName)){
                //friend.setText(":)");
                friend.disable();

                headerPanel.repaint();
            } else{
                userPanel.friendChanger(userName,userIcon);
                //friend.setText(":(");
                showPopup();
                headerPanel.repaint();
            }
        });


        friend.setSize(35,35);
        friend.setLocation(315,-2);
        friend.setBorder(null);

        if(userPanel.checkFriend(userName)){
            //friend.setText(":)");
            friend.disable();
        } else{
            //friend.setText(":(");
            friend.setIcon(new ImageIcon(new ImageIcon("src/resources/friend.png").getImage().getScaledInstance(15, 15, Image.SCALE_DEFAULT)));
        }

        messageView = new JScrollPane();
        messageView.setSize(350, 450);
        messageView.setLocation(0, 30);
        messageView.setBackground(Color.gray);
        messageView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        messageView.setViewportView(messagePanel);
        add(messageView);

        headerLabel = new JLabel(userName);
        headerLabel.setFont(new Font("ITALIC", Font.BOLD, 16));
        headerLabel.setBounds(145, 0, 100, 30);
        headerLabel.setVisible(true);
        headerPanel.add(headerLabel);


        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose an image file");

        picture.addActionListener(this);
        //picture.setText("Image");
       // picture.setFont(new Font("ITALIC",0,10));
        picture.setIcon(new ImageIcon(new ImageIcon("src/resources/imageIcon.png").getImage().getScaledInstance(60, 40, Image.SCALE_DEFAULT)));
        picture.setSize(70, 40);
        picture.setLocation(280, 480);
        add(picture);

        send.addActionListener(this);
        send.setText("Image");
        send.setFont(new Font("ITALIC",0,10));
        send.setSize(70, 40);
        send.setLocation(280, 480);
        add(send);

        textField.setToolTipText("Wanna cookie?");
        textField.setSize(280, 40);
        textField.setLocation(3, 480);
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendText();
            }
        });
        add(textField);

    }

    /**
     * This method is used for making animation for "Friend added" textarea which is pop-ups from right side
     * and slides back.
     */
    private void showPopup() {
        Timer slideOut = new Timer(15, new ActionListener() {
            int xAxis = 350;

            @Override
            public void actionPerformed(ActionEvent e) {
                xAxis -= 10;
                popup.setLocation(xAxis,40);
                if (xAxis <= 250) {
                    ((Timer) e.getSource()).stop();
                    Timer timer = new Timer(2000, new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ((Timer)e.getSource()).stop();
                            Timer timer = new Timer(15, new ActionListener() {
                                int xAxis = 250;

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    xAxis += 10;
                                    popup.setLocation(xAxis,40);
                                    if (xAxis >= 350) {
                                        ((Timer) e.getSource()).stop();
                                    }
                                }
                            });
                            timer.start();
                        }
                    });
                    timer.start();
                }
            }
        });
        slideOut.start();
    }

    /**
     * This method returns name of receiver.
     * @return Username of receiver.
     */
    public String getName() {
            return userName;
    }

    /**
     * This method sending text message to clientGUI and show them on scrollable panel.
     */
    private void sendText(){
        String text = textField.getText();
        clientGUI.sendMessage(userName, null, text);

        RoundedTextArea message = new RoundedTextArea(10);
        message.append("You" + " " + formattedDate.toString() + "\n"+ text);
        message.setEditable(false);
        message.setSize(155,60);
        message.setLineWrap(true);
        message.setLocation(172, yAxis);
        message.setBackground(Color.yellow);
        message.setVisible(true);
        messages.add(message);

        messagePanel.setPreferredSize(new Dimension(310, yAxis+62));
        messagePanel.setBackground(Color.white);
        messagePanel.validate();
        messagePanel.repaint();

        for (JTextArea showMessage : messages) {
            messagePanel.add(showMessage);
        }

        messageView.setViewportView(messagePanel);
        messageView.getVerticalScrollBar().setValue(messageView.getVerticalScrollBar().getMaximum());
        messageView.validate();
        messageView.repaint();

        yAxis += 65;
        textField.setText("");
    }

    /**
     * This method sends image to receiver and show it on message panel.
     * @param imageIcon Image to send and show.
     */
    private void sendImage(ImageIcon imageIcon){

        clientGUI.sendMessage(userName, imageIcon, null);


        JLabel image = new JLabel();
        image.setIcon(new ImageIcon(new ImageIcon(selectedFile.getAbsolutePath()).getImage().getScaledInstance(115, 115, Image.SCALE_DEFAULT)));
        image.setSize(120,120);
        image.setLocation(200, yAxis);
        image.setBackground(Color.yellow);
        image.setVisible(true);
        imageContainer.add(image);

        messagePanel.setPreferredSize(new Dimension(310, yAxis+120));
        messagePanel.setBackground(Color.white);
        messagePanel.validate();
        messagePanel.repaint();

        for (JLabel images : imageContainer) {
            messagePanel.add(images);
        }

        messageView.setViewportView(messagePanel);
        messageView.getVerticalScrollBar().setValue(messageView.getVerticalScrollBar().getMaximum());
        messageView.validate();
        messageView.repaint();

        yAxis += 125;
        textField.setText("");
    }

    /**
     * This method getting message from clientGUI and displays it on message panel. It can be Either text or image.
     * @param userName Sender username.
     * @param icon Image to show.
     * @param textToShow Message to show.
     */
    public void getMessage(String userName, ImageIcon icon, String textToShow){
        if (textToShow != null){

        RoundedTextArea message = new RoundedTextArea(10);
        message.append(userName + " " + formattedDate.toString() + "\n"+ textToShow);
        message.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        message.setForeground(Color.white);
        message.setEditable(false);
        message.setSize(150,60);
        message.setLineWrap(true);
        message.setLocation(2, yAxis);
        message.setBackground(Color.black);
        message.setVisible(true);
        messages.add(message);

        messagePanel.setPreferredSize(new Dimension(310, yAxis+62));
        messagePanel.setBackground(Color.white);
        messagePanel.validate();
        messagePanel.repaint();
        for (JTextArea showMessage : messages) {
            messagePanel.add(showMessage);
        }

        messageView.setViewportView(messagePanel);
        messageView.getVerticalScrollBar().setValue(messageView.getVerticalScrollBar().getMaximum());
        messageView.validate();
        messageView.repaint();

        yAxis += 65;
        textField.setText("");
        }
        if(icon != null){
            JLabel image = new JLabel();
            image.setIcon(icon);
            image.setSize(120,120);
            image.setLocation(2, yAxis);
            image.setBackground(Color.yellow);
            image.setVisible(true);
            imageContainer.add(image);

            messagePanel.setPreferredSize(new Dimension(310, yAxis+120));
            messagePanel.setBackground(Color.white);
            messagePanel.validate();
            messagePanel.repaint();

            for (JLabel images : imageContainer) {
                messagePanel.add(images);
            }

            messageView.setViewportView(messagePanel);
            messageView.getVerticalScrollBar().setValue(messageView.getVerticalScrollBar().getMaximum());
            messageView.validate();
            messageView.repaint();

            yAxis += 125;
            textField.setText("");
        }
    }

    /**
     * This method activates buttons on message panel.
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == goBack) {
            clientGUI.closeChat(userName);
        } else if (e.getSource() == send) {
           sendText();

        } else if(e.getSource() == picture){
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();

                ImageIcon image = new ImageIcon(new ImageIcon(selectedFile.getAbsolutePath()).getImage().getScaledInstance(115, 115, Image.SCALE_DEFAULT));

                sendImage(image);
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

