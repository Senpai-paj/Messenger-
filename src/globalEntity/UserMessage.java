package globalEntity;

public class UserMessage extends Message {
    private User user;

    /**
     *
     * @param user
     */
    public UserMessage(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
