package globalEntity;

import java.util.ArrayList;

public class OnlineListMessage extends Message{

    private ArrayList<User> userList;

    /**
     *
     * @param userList
     */
    public OnlineListMessage(ArrayList<User> userList) {
        this.userList = userList;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
    }
}
