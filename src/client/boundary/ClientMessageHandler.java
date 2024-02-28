package client.boundary;

import client.controller.ClientController;
import globalEntity.*;

import java.io.*;
import java.net.Socket;

/**
 * This class intends to handle all the incoming and outgoing messages to the server.
 */
public class ClientMessageHandler implements Serializable {
    private ClientController controller;
    private Listener thread;
    private String ip;
    private int port;

    /**
     * Constructor that gets the IP-address, port and the current User that is later sent to the server.
     * It then starts the Listener thread.
     * @param ip the IP-address
     * @param port the port number
     * @param clientController the ClientController object. Intends to enable communication back and forth.
     * @param loggedInUser the current user that wants to log in.
     */
    public ClientMessageHandler(String ip, int port, ClientController clientController, User loggedInUser) {
        this.controller = clientController;
        this.ip = ip;
        this.port = port;
        thread = new Listener(loggedInUser);
        thread.start();
    }

    /**
     * Method that gets a message from ClientController and sends it to the server.
     * @param chatMessageToSend the message it sends to the server.
     */
    public void sendMessage(ChatMessage chatMessageToSend) {
        try {
            thread.oos.writeObject(chatMessageToSend);
        } catch (IOException e) {
            ClientGUI.printSystemMessage(e.getMessage());
            ClientGUI.printSystemMessage("Could not send a ChatMessage to server");
        }
    }

    /**
     * Inner class that opens a connection to the server in order to communicate through object streams.
     */
    private class Listener extends Thread {
        private ObjectOutputStream oos;
        private ObjectInputStream ois;
        private final User userWantingToConnect;

        /**
         * Constructor that receives User in order to send it to the server.
         * @param userWantingToLogIn is the current user that sends a request to the server to log in.
         */
        public Listener(User userWantingToLogIn) {
            this.userWantingToConnect = userWantingToLogIn;
            System.out.println("Created listener in ClientMessageHandler");
        }

        /**
         * Starts new thread and open socket in ClientMessageHandler and open ObjectStream in ClientMessageHandler to send User-object from ClientMessageHandler
         * and connect client. The endless loop reads Message-objects from server and calls on the receiveObject method in ClientController.
         */
        @Override
        public void run() {
            try(Socket socket = new Socket(ip, port)) {

                System.out.println("Opened socket in ClientMessageHandler");
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
                System.out.println("Opened objectStreams in ClientMessageHandler");

                oos.writeObject(new UserMessage(userWantingToConnect));
                System.out.println("Sent User object from ClientMessageHandler");

                ClientGUI.printSystemMessage("Client connected");

                while (true) {
                    Message message = (Message) ois.readObject();
                    System.out.println("ClientMessageHandler.java: read object from server");
                    controller.receiveObject(message);
                }

            } catch(IOException | ClassNotFoundException e) {
                //TODO: Check this: throw new RuntimeException(e);
            }
        }
    }

}
