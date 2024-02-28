package globalEntity;

import javax.swing.*;

public class User extends Message {
    private String userID;
    private ImageIcon userIcon;

    /**
     *
     * @param userID
     * @param userIcon
     */
    public User(String userID, ImageIcon userIcon) {
        super();
        this.userID = userID;
        this.userIcon = userIcon;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ImageIcon getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(ImageIcon userIcon) {
        this.userIcon = userIcon;
    }

    //For comparing User object regardless of userIcon
    @Override
    public int hashCode() {
        return userID.hashCode();
    }

    //For comparing User object regardless of userIcon
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User)
            return userID.equals(((User)obj).getUserID());
        return false;
    }

}
