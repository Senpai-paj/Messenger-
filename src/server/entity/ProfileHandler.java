package server.entity;

import globalEntity.User;

import java.util.ArrayList;

/**
 * This class is responsible for temporarily caching registered users in a list
 */
public class ProfileHandler {
    private final ArrayList<User> registeredUsers;

    /**
     * Takes as input user list to cache in memory
     * @param registeredUsers the list of users to cache
     */
    public ProfileHandler(ArrayList<User> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    /**
     * @return the cached user objects as list
     */
    public ArrayList<User> getRegisteredUsers() {
        return registeredUsers;
    }

    /**
     * Checks if user object is present in cache
     * @param user the user object to be checked
     * @return true if found else false
     */
    public boolean profileExist(User user) {
        return registeredUsers.contains(user);
    }

    /**
     * Adds a user object to the cache
     * @param userToBeRegistered the user to be cached
     */
    public void registerUser(User userToBeRegistered) {
        registeredUsers.add(userToBeRegistered);
    }

}



