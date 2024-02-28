package client.controller;

import client.entity.ContactsManager;
import globalEntity.User;

import javax.swing.*;
import java.util.ArrayList;

public class MainMain {
    public static void main(String[] args) {
        //new ClientController("localhost",420);
        User owner = new User("WTF",new ImageIcon("src/resources/gubbe.jpg"));
        User friend = new User("no",new ImageIcon("src/resources/gubbe.jpg"));
        ArrayList<User> friendsList = new ArrayList<>();
        friendsList.add(friend);
        ContactsManager.saveUserContacts(owner,friendsList);
    }
}
