package server.boundary;

import globalEntity.*;
import server.controller.ServerController;
import server.entity.LogType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Is responsible for handling incoming messages from client and the outgoing messages from the clienthandler
 */
public class ClientConnection extends Thread implements Serializable {
    private User user;
    private Socket socket;
    private ServerController serverController;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private UserMessage userWantToConnect;
    private ClientHandler clientHandler;

    /**
     * Constructor the gets the socket from clienthandler and then starts the thread
     * @param socket the socket from clienthandler
     * @param serverController enabling a connection to the controller
     * @param clientHandler enabling a connection to the clienthandler
     */
    public ClientConnection(Socket socket, ServerController serverController, ClientHandler clientHandler) {
        this.socket = socket;
        this.serverController = serverController;
        this.clientHandler = clientHandler;
        start();
    }

    /**
     * Enables the clienthandler to send messages of both ChatMessage and OnlineListMessage
     * @param messageToSend the incoming message from ClientHandler that is to be sent to the client
     */
    public synchronized void sendMessage(Message messageToSend) {
        try {
            if(messageToSend instanceof ChatMessage) {
                this.oos.writeObject(messageToSend);
                //System.out.println("SENDMESSAGE");
                //System.out.println(messageToSend);
            }

            if (messageToSend instanceof OnlineListMessage)
                this.oos.writeObject(messageToSend);

        } catch (IOException e) {
            //skicka meddelande i log?
            //ServerUI.printSystemMessage(e.getMessage());
            //System.out.println("ClientConnection.java: Can't send a message to client");
        }
    }

    /**
     * Reads UserMessage-object to get the User connected and or registered. Then reads the Message-objects sent to the server and the receiveObject
     * processes the Message-object.
     */
    @Override
    public void run() {
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());

            userWantToConnect = (UserMessage) ois.readObject();
            user = userWantToConnect.getUser();
            serverController.registerUser(userWantToConnect.getUser());

            serverController.addLog(new String[]{getUser().getUserID()}, LogType.UserConnected);


            //System.out.println("ClientConnection.java: New client is ready to go");

            while(true) {
                //chat message from client comming from here
                Message message = (Message) ois.readObject();
                //System.out.println("ClientConnection.java: Client sent message to server");
                clientHandler.receiveObject(message);
            }

        } catch (ClassNotFoundException e) {
            System.err.println(e);
        } catch (IOException e) {
            //System.out.println("ClientConnection.java: Client disconnected");
            clientHandler.disconnectClient(this);
        }
    }

    /**
     * Method that enables the ClientHandler to get the user
     * @return the current user
     */
    public User getUser() {
        return user;
    }

    /**
     * Method that enables the ClientHandler to set the user
     * @param user the new user
     */
    public void setUser(User user) {
        this.user = user;
    }


}
