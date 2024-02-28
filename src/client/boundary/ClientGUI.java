package client.boundary;

import client.controller.ClientController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This is ClientGUI class which is represents main frame for program, and it's connected to ClientController to
 * send and take different variables for system to work.
 */
public class ClientGUI extends JFrame{

    private UserPanel userPanel = new UserPanel(this);
    private ArrayList<MessagePanel> messagePanels = new ArrayList<>();
    private ClientController controller;
    private ClientGUI itself;

    /**
     * This is the constructor of ClientGUI which make main format for window for all object which will be added after.
     * its also bind itself with controller of client.
     * @param controller client controller
     * @param title title is alternative call for username, in this case it's used as title of the frame.
     */
    public ClientGUI(ClientController controller, String title) {
        this.controller = controller;
        this.add(userPanel);
        setResizable(false);
        setTitle(String.format("SnapCrap: %s", title));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setVisible(true);
        itself = this;
        popUp();
    }

    /**
     * This method gets names, images and online status off all online users and friend list.
     * @param strings names off all users.
     * @param imageIcons images of all users.
     * @param online online status of all users.
     */
    public void setUsers(ArrayList<String> strings, ArrayList<ImageIcon> imageIcons, boolean[] online){
        userPanel.setUsers(strings,imageIcons,online,controller.getFavoriteUsersName());
    }

    /**
     * This method updates friend list.
     * @param friends names of friend which is got from controller.
     * @param friendsIcon images of friends which is got from controller.
     */
    public void refreshFriends(ArrayList<String> friends,ArrayList<ImageIcon> friendsIcon){
        System.out.println("send to controller");
        controller.setFavoriteUsersNames(friends,friendsIcon);
    }

    /**
     * This method creates new chat panel which is providing ability to send messages and add friends.
     * @param userName name of interlocutor.
     * @param userIcon image of interlocutor.
     */
    public void createChat(String userName, ImageIcon userIcon) {
        MessagePanel messagePanel = new MessagePanel(userName, this, userPanel, userIcon);
        messagePanel.setLocation(350,0);
        add(messagePanel);
        messagePanels.add(messagePanel);
    }

    /**
     * This method implements animations which is used to move message panel and user panel. It's hiding message panel
     * outside of frame view-field.
     * @param userName username needed to move wright message panel.
     */
    public void closeChat(String userName){
        for(MessagePanel chatPanel: messagePanels){

            if(Objects.equals(chatPanel.getName(), userName)){

                Timer timer = new Timer(4, new ActionListener() {
                    int xAxis = -350;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        xAxis += 10;
                        userPanel.setLocation(xAxis,0);
                        chatPanel.setLocation(350+xAxis,0);
                        if(xAxis == 0){
                            ((Timer)e.getSource()).stop();
                        }
                    }
                });
                timer.start();
            }
        }
    }

    /**
     * This method implements animations which is used to move message panel and user panel. It's hiding user panel
     * outside of frame view-field.
     * @param userName username needed to move wright message panel.
     */
    public void openChat(String userName){
        for(MessagePanel chatPanel: messagePanels){

            if(Objects.equals(chatPanel.getName(), userName)) {

                Timer timer = new Timer(2, new ActionListener() {
                    int xAxis = 0;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        xAxis -= 10;
                        userPanel.setLocation(xAxis, 0);
                        chatPanel.setLocation(350 + xAxis, 0);
                        if (xAxis <= -350) {
                            ((Timer) e.getSource()).stop();
                        }
                    }
                });
                timer.start();
            }
        }

    }

    /**
     * This method checks if message panel with selected user exists.
     * @param userName username which is needed to be checked.
     * @return returns boolean, true if chat exists and false if not.
     */
    public boolean chatExist(String userName){
        for(MessagePanel m : messagePanels){
            if(m.getName() == userName){
                return true;
            }
        }
        return false;
    }

    /**
     * This method gets text or image message from chat and sends it to controller.
     * @param userName Name of receiver of message.
     * @param image Image which can be sent.
     * @param textToSend Text which can be sent.
     */
    public void sendMessage(String userName, ImageIcon image, String textToSend){
        System.out.println("ClientGUI.java: sendMessage:");
        System.out.println(userName);
        controller.sendChatMessage(userName,image,textToSend);
    }

    /**
     * This method gets message from controller and sends it to message panel. If chat does not exist in the moment of
     * receiving it is beaning created.
     * @param userName Name of sender.
     * @param image Image which can be received.
     * @param textToShow Text which can be received.
     */
    public void getMessage(String userName, ImageIcon image, String textToShow){
        if(chatExist(userName)) {
            for (MessagePanel panel : messagePanels) {
                if (Objects.equals(panel.getName(), userName)) {
                    panel.getMessage(userName, image, textToShow);
                }
            }
        }
        else{
            createChat(userName, image);
            for (MessagePanel panel : messagePanels) {
                if (Objects.equals(panel.getName(), userName)) {
                    panel.getMessage(userName, image, textToShow);
                }
            }
        }
    }

    /**
     * This is method which is created to provide masterpiece animation which makes using program possible.
     */
    private void popUp(){
        Timer timer = new Timer(1, new ActionListener() {
            int xAxis = 0;
            int yAxis = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if(xAxis < 350){
                    xAxis += 10;
                }
                if(yAxis < 550){
                    yAxis += 10;
                }
                itself.setSize(xAxis, yAxis);
                itself.setLocationRelativeTo(null);
                if (xAxis >= 350 && yAxis >= 550) {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }

    /**
     * This method is used to provide internal system messages.
     * @param text text to show in terminal.
     */
    public static void printSystemMessage(String text) {
        System.out.println(text);
    }
}
