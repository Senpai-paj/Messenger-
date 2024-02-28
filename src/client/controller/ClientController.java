package client.controller;

import client.boundary.ClientGUI;
import client.boundary.ClientMessageHandler;
import client.boundary.LoginClient;
import client.entity.ContactsManager;
import globalEntity.ChatMessage;
import globalEntity.Message;
import globalEntity.OnlineListMessage;
import globalEntity.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * This class is responsible for communicating with a chat server and handling user contacts.
 * @author Johannes Rosengren (DatorID: an6380
 * @author Fabian Kjellberg datorid: an5883
 */
public class ClientController {
    private User loggedInUser;
    private ClientGUI clientGUI;
    private ClientMessageHandler clientMessageHandler;
    private LoginClient loginClient;
    private String ip;
    private int port;

    //Test variabler
    //private final ImageIcon testImage  = new ImageIcon(new ImageIcon("src/resources/gubbe.jpg").getImage().getScaledInstance(40, 40,Image.SCALE_DEFAULT));

    /**
     * Attempts to establish communication with server on specified ip & port
     * @param ip the ip to use
     * @param port the port to use
     * @author Johannes Rosengren (DatorID: an6380)
     */
    public ClientController(String ip, int port) {
        this.ip = ip;
        this.port = port;

        loginClient = new LoginClient(this);
        loginClient.setVisible(true);
    }

    /**
     * Starts the client with the specified user details
     * @param userID the userID to use
     * @param imageIcon the image icon to use
     * @author Fabian Kjellberg datorid: an5883
     */
    public void startClient(String userID, ImageIcon imageIcon){
        //loginClient.dispose();

        Image img = imageIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(img);

        authenticate(userID,resizedIcon);
    }

    /**
     * formats arrays to send to the client via strings, images and booleans (method never used due to different formatting)
     * @param connectedUsers
     * @author Fabian Kjellberg datorid: an5883
     */
    public void updateConnectedUsers(OnlineListMessage connectedUsers) {

        ArrayList<User> onlineListMessage = connectedUsers.getUserList();
        ArrayList<User> offlineContactsList = ContactsManager.fetchUserContacts(loggedInUser);

        for (int i = 0; i < onlineListMessage.size(); i++){
            System.out.println(onlineListMessage.get(i).getUserID());
        }

        //remove own user
        onlineListMessage.remove(loggedInUser);

        //remove online users from offline list
        for (int i = 0; i < offlineContactsList.size(); i++){
            try{
                offlineContactsList.remove(onlineListMessage.get(i));
            }
            catch (Exception e){}
        }

        //formats the arrays to be used for the viewer
        int totalLength = onlineListMessage.size() + offlineContactsList.size() +1;
        String[] userName = new String[totalLength];
        ImageIcon[] userIcon = new ImageIcon[totalLength];
        boolean[] online = new boolean[totalLength];
        User currentUser;

        //adds groupChat
        userName[0] = "all";
        online[0] = false;

        //adds online users to the arrays
        for (int i = 0; i < onlineListMessage.size(); i++) {
            currentUser = onlineListMessage.get(i);
            userName[i+1] = currentUser.getUserID();
            userIcon[i+1] = currentUser.getUserIcon();
            online[i+1] = true;
            System.out.println(String.format("[%s]UserName: %s, status: Online",i+1, userName[i+1])); // debugkod
        }
        //adds offline users to the array
        int j = 0;
        for (int i = onlineListMessage.size() + 1; i < totalLength; i++){
            System.out.println(offlineContactsList.get(j));
            currentUser = offlineContactsList.get(j);
            userName[i+1] = currentUser.getUserID();
            userIcon[i+1] = currentUser.getUserIcon();
            online[i+1] = false;
            System.out.println(String.format("[%s]UserName: %s, status: Offline",i+1, userName[i+1])); // debugkod
            j++;
        }

        //clientGUI.setUsers(userName, userIcon, online);

    }

    /**
     * Fetches online users & contacts of the current user and updates GUI
     * @param connectedUsers the onlineListMessage with all online users
     * @author Johannes Rosengren (DatorID: an6380)
     * @author Fabian Kjellberg datorid: an5883
     */
    private void refreshConnectedUsersList(OnlineListMessage connectedUsers) {
        ArrayList<User> onlineListMessage = connectedUsers.getUserList();
        ArrayList<User> contactsList = new ArrayList<>(ContactsManager.fetchUserContacts(loggedInUser));
        onlineListMessage.remove(loggedInUser);

        ArrayList<String> userID = new ArrayList<>();
        ArrayList<ImageIcon> userImage = new ArrayList<>();
        ArrayList<Boolean> onlineTrueFalse = new ArrayList<>();

        for (User currUser : onlineListMessage) {
            userID.add(currUser.getUserID());
            userImage.add(currUser.getUserIcon());
            onlineTrueFalse.add(true);
        }

        for (User currContact : contactsList) {
            if (!userID.contains(currContact.getUserID())) {
                userID.add(currContact.getUserID());
                userImage.add(currContact.getUserIcon());
                onlineTrueFalse.add(false);
            }
        }

        userID.add("PM to all");
        userImage.add(new ImageIcon(new ImageIcon("src/resources/gubbe.jpg").getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT)));
        onlineTrueFalse.add(true);

        boolean[] sendBool = new boolean[onlineTrueFalse.size()];
        for (int i = 0; i < onlineTrueFalse.size(); i++) {
            sendBool[i] = onlineTrueFalse.get(i);
        }

        clientGUI.setUsers(userID,userImage,sendBool);
    }

    /**
     * The sendChatMessageIsCalled from the GUI, the method then creates a chatMessage that
     * is sent to ClientMessageHandler
     * @param userName a string that is used for the parameter to construct a ChatMessage which will be the reciever of the message
     * @param imageToSend an image that can be sent through a message, if there is none it is left as null
     * @param textToSend a text that can be sent through a message, if there is none it is left as null
     * @author Fabian Kjellberg DatorID: 5883
     */
    public void sendChatMessage(String userName, ImageIcon imageToSend, String textToSend) {
        ChatMessage chatMessageToSend = new ChatMessage(userName,textToSend,imageToSend, loggedInUser.getUserID());
        clientMessageHandler.sendMessage(chatMessageToSend);
    }

    /**
     * Builds User object from input. Fetches associated contacts from disk. Starts ClientGUI with User + contacts
     * @param userName userName used to build User object
     * @param userImage userImage used to build User object
     * @author Johannes Rosengren (DatorID: an6380)
     */
    public void authenticate(String userName, ImageIcon userImage) {
        this.loggedInUser = new User(userName,userImage);
        this.clientMessageHandler = new ClientMessageHandler(ip,port,this,loggedInUser);
        this.clientGUI = new ClientGUI(this, loggedInUser.getUserID());
    }

    /**
     * Method that processes subclasses of the Message class
     * @param inputMessage the Message subclass to be processed
     * @author Johannes Rosengren (DatorID: an6380)
     */
    public void receiveObject(Message inputMessage) {
        if (inputMessage instanceof OnlineListMessage) {
            //System.out.println("ClientController.java: receiveObject()");
            //System.out.println(((OnlineListMessage) inputMessage).getUserList());
            refreshConnectedUsersList((OnlineListMessage) inputMessage);
        }
        else if (inputMessage instanceof ChatMessage) {
            //System.out.println("Received chat message");
            clientGUI.getMessage(((ChatMessage) inputMessage).getSender(),((ChatMessage) inputMessage).getImage(),((ChatMessage) inputMessage).getText());
        }
        else {
            ClientGUI.printSystemMessage("Could not read messageType");
        }
    }

    /**
     * Fetches the logged-in user's contacts as strings
     * @return list with string representation of user contact list
     * @author Johannes Rosengren (DatorID: an6380)
     */
    public ArrayList<String> getFavoriteUsersName() {
        ArrayList<String> strOutList = new ArrayList<>();

        ArrayList<User> contactsInputList = new ArrayList<>(ContactsManager.fetchUserContacts(loggedInUser));

        contactsInputList.forEach((user)-> strOutList.add(user.getUserID()));

        return strOutList;
    }

    /**
     * Overwrites user list with new one built from inputted lists of userIDs & userImages.
     * Both lists need to be in sync.
     * @param userIDs the userIDs to be saved
     * @param userImages the userImages to be saved
     * @author Johannes Rosengren (DatorID: an6380)
     */
    public void setFavoriteUsersNames(ArrayList<String> userIDs, ArrayList<ImageIcon> userImages) {
        ArrayList<User> outputList = new ArrayList<>();

        for (int i = 0; i < userIDs.size(); i++) {
            outputList.add(new User(userIDs.get(i),userImages.get(i)));
        }

        //remove group chat user if it for some reason got added as contact
        outputList.removeIf(user -> Objects.equals(user.getUserID(), "PM to all"));

        //makes sure there are no duplicates in contact list to be written
        LinkedHashSet<User> hashSet = new LinkedHashSet<>(outputList);
        outputList = new ArrayList<>(hashSet);

        ContactsManager.saveUserContacts(loggedInUser,outputList);
    }

}
