package server.entity;

import globalEntity.User;

import java.io.*;
import java.util.ArrayList;

/**
 * This class is responsible for loading and saving user lists on disk
 */
public class UserManager {
    /**
     * @param usersToSave the user list to save to disk
     */
    public static synchronized void saveRegisteredUsersToDisk(ArrayList<User> usersToSave) {
        File saveFile = new File("src/resources/"+"registered_users.dat");

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(saveFile))) {
            objectOutputStream.writeObject(usersToSave);
        } catch (IOException e) {
            //System.out.println("UserManager.java: Could not save registered users");
        }
    }

    /**
     * @return the user list retrieved from disk
     */
    public static synchronized ArrayList<User> loadRegisteredUsersFromDisk() {
        File loadFile = new File("src/resources/"+"registered_users.dat");

        if (loadFile.exists() && loadFile.isFile()) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(loadFile))) {
                ArrayList<User> userList = (ArrayList<User>) objectInputStream.readObject();
                if (userList != null)
                    if (userList.size() > 0)
                        return userList;
            } catch (IOException e) {
                //System.out.println("UserManager.java: Could not load ArrayList from disk: IOException");
            } catch (ClassNotFoundException e) {
                //System.out.println("UserManager.java: Could not load ArrayList from disk: ClassNotFoundException");
            }
        }
        return new ArrayList<User>();
    }
}
