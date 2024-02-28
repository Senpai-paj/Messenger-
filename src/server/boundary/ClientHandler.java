package server.boundary;

import globalEntity.ChatMessage;
import globalEntity.Message;
import globalEntity.OnlineListMessage;
import globalEntity.User;
import server.controller.ServerController;
import server.entity.LogType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This class intends to handle the clients requesting to connect.
 */
public class ClientHandler {
    private ArrayList<ClientConnection> connectedUsers; //TODO ändra List till ArrayList i klassdiagrammet
    private ServerController serverController;

    /**
     * Constructor that receives port number and the ServerController object and starts the Connection thread.
     * @param port
     * @param serverController
     */
    public ClientHandler(int port, ServerController serverController) {
        this.serverController = serverController;
        connectedUsers = new ArrayList<>();
        new Connection(port,this).start();
    }

    /**
     * Called when Message is received from a client and check if it is a ChatMessage-object and if it is going to be sent
     * to a group chat with multiple users or to a single user.
     * @param inputMessage the incoming Message
     */
    public void receiveObject(Message inputMessage) {
        if (inputMessage instanceof ChatMessage) {
            //check if group chat
            if (Objects.equals(((ChatMessage) inputMessage).getReceiver(), "PM to all")) {
                //System.out.println("GROUP MESSAGE TIME");
                serverController.groupChatPush((ChatMessage) inputMessage);
            }
            //if not group chat - regular message
            else {
                serverController.bufferMessage((ChatMessage) inputMessage);
                serverController.addLog(
                        new String[]{((ChatMessage) inputMessage).getSender(), ((ChatMessage) inputMessage).getReceiver()}, LogType.MessageSent);
            }
        }
    }

    /**
     * When a user disconnects the method removes the user from the list of connected users.
     * It then informs the log.
     * @param clientConnection
     */
    public void disconnectClient(ClientConnection clientConnection) {
        //System.out.println("ClientHandler.java: disconnectClient triggered");
        //System.out.println("ClientHandler.java: list of users before disconnect");
        //System.out.println(connectedUsers);
        connectedUsers.remove(clientConnection);
        serverController.userListChangeEvent();
        //System.out.println("ClientHandler.java: list of users after disconnect");
        //System.out.println(connectedUsers);
        serverController.addLog(new String[]{clientConnection.getUser().getUserID()}, LogType.UserDisconnected);
    }

    /**
     * Intends to update the list of connected users.
     */
    public void pushOnlineList() {
        //System.out.println("Entering ClientHandler.java: pushOnlineList()");
        ArrayList<User> tmpList = new ArrayList<>();

        for (int i = 0; i < connectedUsers.size(); i++) {
            tmpList.add(connectedUsers.get(i).getUser());
        }

        //System.out.println("ClientHandler.java: online list about to be sent:");
        //System.out.println(tmpList);
        //System.out.println("ClientHandler.java: receiver list:");
        //System.out.println(connectedUsers);

        OnlineListMessage onlineListMessage = new OnlineListMessage(tmpList);
        for (int i = 0; i < connectedUsers.size(); i++) {
            connectedUsers.get(i).sendMessage(onlineListMessage);
        }

    }

    /**
     * Sends a ChatMessage to the selected user.
     * @param messageToSend
     */
    public void pushMessage(ChatMessage messageToSend) {
        for (ClientConnection currentClient : connectedUsers) {
            if (Objects.equals(messageToSend.getReceiver(), currentClient.getUser().getUserID())) {
                currentClient.sendMessage(messageToSend);
                serverController.addLog(new String[]{messageToSend.getReceiver(), messageToSend.getSender()}, LogType.MessageReceived);
            }
        }
    }

    /**
     * Returns the list of users that are connected to the server
     * @return ArrayList of connected users
     */
    public ArrayList<User> fetchOnlineList() {
        ArrayList<User> tmpList = new ArrayList<>();
        for (ClientConnection currentClient : connectedUsers)
            tmpList.add(currentClient.getUser());
        return tmpList;
    }

    /**
     * Returns the list of user that are connected to the server as strings
     * @return ArrayList of connected users as strings
     */
    public ArrayList<String> fetchOnlineListAsStrings() {
        ArrayList<String> tmpList = new ArrayList<>();
        for (ClientConnection currentClient : connectedUsers)
            tmpList.add(currentClient.getUser().getUserID());
        return tmpList;
    }

    /**
     * Inner class that handles connections from users.
     * When a user connects, a new ClientConnection is created with the socket.
     */
    private class Connection extends Thread {
        private int port;
        private ClientHandler outerClass;

        public Connection(int port, ClientHandler outerClass) {
            this.port = port;
            this.outerClass = outerClass;
        }

        /**
         * When thread is started this method loops in order to enable users to connect.
         * When a user is connected a new ClientConnection is created with the connected socket.
         */
        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while(true) {
                    Socket socket = serverSocket.accept();
                    ClientConnection clientConnection = new ClientConnection(socket, serverController, outerClass);
                    connectedUsers.add(clientConnection);
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}
