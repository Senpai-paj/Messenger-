package client.entity;

import globalEntity.User;

import java.io.*;
import java.util.ArrayList;

/**
 * This class is responsible for saving and loading contacts (User objects) from disk
 * @author Johannes Rosengren (DatorID: an6380)
 */
public class ContactsManager {
    /**
     * Returns the specified user's contacts if they exist on disk else an empty ArrayList is returned
     * @param ownerOfContacts which user's contacts to retrieve
     * @return the specified user's contacts from disk
     * @author Johannes Rosengren (DatorID: an6380)
     */
    public static ArrayList<User> fetchUserContacts(User ownerOfContacts) {
        File saveFile = new File("src/resources/"+ ownerOfContacts.getUserID().toLowerCase()+".dat");

        if (saveFile.exists() && saveFile.isFile()) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(saveFile))) {
                ArrayList<User> userContactsList = (ArrayList<User>) objectInputStream.readObject();
                if (userContactsList != null)
                    if (userContactsList.size() > 0)
                        return userContactsList;
            } catch (IOException e) {
                //System.out.println("ContactsManager.java: Could not load ArrayList from disk: IOException");
            } catch (ClassNotFoundException e) {
                //System.out.println("ContactsManager.java: Could not load ArrayList from disk: ClassNotFoundException");
            }
        }

        return new ArrayList<>();
    }

    /**
     * Takes as input an ArrayList with User objects representing a User's contacts
     * @param ownerOfContacts which user's contacts to retrieve
     * @param contactListToSave the ArrayList with User objects (contacts) which is to be saved on disk
     * @author Johannes Rosengren (DatorID: an6380)
     */
    public static void saveUserContacts(User ownerOfContacts, ArrayList<User> contactListToSave) {
        File saveFile = new File("src/resources/"+ownerOfContacts.getUserID().toLowerCase()+".dat");
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(saveFile))) {
            objectOutputStream.writeObject(contactListToSave);
        } catch (IOException e) {
            //System.out.println("ContactsManager.java: Could not save contactList");
        }
    }

}
