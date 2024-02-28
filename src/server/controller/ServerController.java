package server.controller;

import globalEntity.ChatMessage;
import globalEntity.User;
import server.boundary.ClientHandler;
import server.boundary.ServerUI;
import server.entity.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * This class is responsible for managing communications between clients that connect to the server.
 */
public class ServerController {
    private final HashMap<String, ArrayList<ChatMessage>> unhandledMessages;
    private final ClientHandler clientHandler;
    private final ProfileHandler profileHandler;
    private final ServerUI serverUI;
    private final LogHandler logHandler;

    /**
     * Starts up server. Loads registered users from disk. Starts listening for incoming clients on port 420
     */
    public ServerController() {
        logHandler = new LogHandler();

        profileHandler = new ProfileHandler(UserManager.loadRegisteredUsersFromDisk());

        unhandledMessages = new HashMap<>();
        initHashMap();

        clientHandler = new ClientHandler(420,this);

        serverUI = new ServerUI(this);
    }

    /**
     * displays all the logs in the system (never implemented only used for testing)
     * @param beginTime
     * @param endTime
     */
    public void displayLogs(LocalDateTime beginTime, LocalDateTime endTime){
        ArrayList<Log> logsToDisplay = logHandler.getLogs(beginTime,endTime);
        ArrayList<String> logStrings = new ArrayList<String>();
        for (Log log : logsToDisplay) {
            logStrings.add(log.toString());
        }
        serverUI.displayMessages(logStrings);
    }

    /**
     * Populates hashmap with already registered users
     */
    private void initHashMap() {
        ArrayList<User> allRegisteredUsersList = profileHandler.getRegisteredUsers();
        for (User currentUser : allRegisteredUsersList) {
            unhandledMessages.put(currentUser.getUserID(), new ArrayList<ChatMessage>());
        }
    }

    /**
     * Buffers a message in the system to its intended receiver(s).
     * Delivers buffered message if receiver(s) are online
     * @param incomingChatMessage the incoming ChatMessage
     */
    public void bufferMessage(ChatMessage incomingChatMessage) {
        //check if receiver is present in hashMap else something has gone wrong
        if (unhandledMessages.containsKey(incomingChatMessage.getReceiver())) {
            unhandledMessages.get(incomingChatMessage.getReceiver()).add(incomingChatMessage);
            if (clientHandler.fetchOnlineListAsStrings().contains(incomingChatMessage.getReceiver())) {
                deliverMessagesTo(incomingChatMessage.getReceiver());
            }
        }
        else System.err.println("ServerController.java: bufferMessage(): Could not find receiver for message");
    }

    /**
     * Pushes a chat message to all users online
     * @param incomingChatMessage chat message to push to group chat
     */
    public void groupChatPush(ChatMessage incomingChatMessage) {
        ArrayList<User> currentlyOnline = new ArrayList<>(clientHandler.fetchOnlineList());
        currentlyOnline.removeIf(user -> Objects.equals(user.getUserID(), incomingChatMessage.getSender()));

        for (User currentUser : currentlyOnline) {
            incomingChatMessage.setReceiver(currentUser.getUserID());
            bufferMessage(incomingChatMessage);
        }
    }

    /**
     * Called when a new or existing user registers on server.
     * @param userToRegister user to register
     */
    public synchronized void registerUser(User userToRegister) {
        if (!profileHandler.profileExist(userToRegister)) {
            profileHandler.registerUser(userToRegister);
            unhandledMessages.put(userToRegister.getUserID(),new ArrayList<ChatMessage>());
        }
        userListChangeEvent();
        //push messages that were stored while client was offline
        deliverMessagesTo(userToRegister.getUserID());
    }

    /**
     * Push updated user list to all connected clients
     */
    public void userListChangeEvent() {
        clientHandler.pushOnlineList();
    }

    /**
     * Deliver message to specified user if online else keep message on server until online
     * @param receivingUser the receiving user
     */
    public void deliverMessagesTo(String receivingUser) {
        if (clientHandler.fetchOnlineListAsStrings().contains(receivingUser)) {
            synchronized (unhandledMessages.get(receivingUser)) {
                ArrayList<ChatMessage> messagesToDeliver = new ArrayList<>(unhandledMessages.get(receivingUser));

                while (messagesToDeliver.size() > 0) {
                    ChatMessage currentMessage = messagesToDeliver.get(0);
                    clientHandler.pushMessage(currentMessage);
                    messageDelivered(currentMessage);
                    messagesToDeliver.remove(currentMessage);
                }
            }
        }
    }

    /**
     * Called to remove delivered message from server storage
     * @param message delivered message to be removed from server
     */
    public void messageDelivered(ChatMessage message) {
        unhandledMessages.get(message.getReceiver()).remove(message);
    }

    /**
     * calls the loghandler to get logs based on a begin time and end time to get logs from a timespan.
     * @param beginTime begin time is from the time you want to receive logs from
     * @param endTime end time is to the time you want to receive logs from
     * @return returns an arraylist of strings.
     */
    public ArrayList<String> getLogs(LocalDateTime beginTime, LocalDateTime endTime) {
        ArrayList<String> stringsToDisplay = new ArrayList<>();

        ArrayList<Log> logArray = logHandler.getLogs(beginTime, endTime);
        for (Log log : logArray) {
            stringsToDisplay.add(log.toString());
        }
        return stringsToDisplay;
    }

    /**
     * Saves cached logs in server memory to disk
     */
    public void saveLogsToServer() {
        logHandler.saveLogs();
    }

    public void addLog(String[] text, LogType logType){
        logHandler.addLog(text,logType);
    }

    /**
     * Saves logs & all cached users then shuts down server
     */
    public void shutdown() {
        saveLogsToServer();
        UserManager.saveRegisteredUsersToDisk(profileHandler.getRegisteredUsers());
        System.exit(0);
    }
}
