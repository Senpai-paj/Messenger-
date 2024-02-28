package server.entity;

/**
 * The Logtype enum is used to differentiate what type of log is created which will be formatted and displayed
 * differently by the system
 */
public enum LogType {
    /**
     * String[] text = [userID]
     */
    UserConnected,
    /**
     * String[] text = [senderID][receiverID]
     */
    MessageSent,
    /**
     * String[] text = [full log text]
     */
    TestLog,
    /**
     * String[] text = [userID]
     */
    UserRegistered,
    /**
     * String[] text = [receiverID][senderID]
     */
    MessageReceived,
    /**
     * String[] text = [userID]
     */
    UserDisconnected
}
