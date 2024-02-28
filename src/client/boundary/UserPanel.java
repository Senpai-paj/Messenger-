package client.boundary;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This class shows up all online users and friends.
 */
public class UserPanel extends JPanel implements ActionListener {

    private JPanel onlineIndicator[];
    private JButton usersButtons[];
    private ClientGUI clientGUI;
    private JScrollPane scrollPane;
    private JPanel innerPanel;
    private ArrayList<String> allUsers;
    private ArrayList<String> friends = new ArrayList<>();
    private boolean[] isFriend;
    private ArrayList<ImageIcon> friendsIcon = new ArrayList<>();
    private ArrayList<ImageIcon> userIcon;

    /**
     * This is the constructor of ClientGUI which make main format for window for all object which will be added after.
     * @param clientGUI main frame for user panel.
     */
    public UserPanel(ClientGUI clientGUI){
        setSize(350, 500);
        setBackground(Color.yellow);
        setLocation(0,0);
        setVisible(true);
        setLayout(null);
        this.clientGUI = clientGUI;

        // Create a new inner panel to hold the buttons and online indicators
        innerPanel = new JPanel();
        innerPanel.setBackground(Color.yellow);
        innerPanel.setLayout(null);

        // Create a scroll pane with the inner panel as its viewport
        scrollPane = new JScrollPane(innerPanel);
        scrollPane.setBounds(0, 0, 350, 500);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane);
    }

    /**
     * This method initializes all parameter which have gotten from clientGUI and ClientController regardenly.
     * @param userName List of all users.
     * @param userIcon List of all users images.
     * @param friends Friend list.
     */
    private void setInstances(ArrayList<String> userName, ArrayList<ImageIcon> userIcon, ArrayList<String> friends){
        //System.out.println("populate kontakts");
        this.usersButtons = new JButton[userName.size()];
        this.onlineIndicator = new JPanel[userName.size()];
        this.allUsers = userName;
        this.userIcon = userIcon;

        for (int i = 0; i < allUsers.size(); i++) {
            for(String name : friends){
                if(Objects.equals(name, allUsers.get(i))){
                    friendsIcon.add(userIcon.get(i));
                }
            }
        }
        this.friends = friends;
        isFriend = new boolean[friends.size()];
        if(isFriend.length == 0){
            boolean[] temporaryFriends = new boolean[isFriend.length+2];
            isFriend = temporaryFriends;
        }
        for (int i = 0; i < isFriend.length; i++) {
            for(String name : friends){
                if (Objects.equals(allUsers.get(i), name)) {
                    if (isFriend[i]){
                        isFriend[i] = true;
                    }
                    break;
                }
            }
        }
    }

    /**
     * This method gets all users from clientGUI (which is gotten from ClientController) and creates buttons which
     * are represents current users and friends, its also fetching online status of users.
     * @param userName  All users.
     * @param userIcon Images of all users.
     * @param onlineStatus Online status off all users.
     * @param friends Friend list.
     */
    public void setUsers(ArrayList<String> userName, ArrayList<ImageIcon> userIcon, boolean[] onlineStatus, ArrayList<String> friends){
        setInstances(userName,userIcon,friends);
        innerPanel.removeAll();

        for (int i = 0; i < usersButtons.length; i++) {

            boolean friend = false;

            for(String name : friends){
                if (Objects.equals(name, userName.get(i))) {
                    friend = true;
                    break;
                }
            }

            if(friend){
                //System.out.println("inside friend if");

                usersButtons[i] = new JButton(userName.get(i));
                usersButtons[i].setSize(320,60);
                usersButtons[i].setBackground(Color.yellow);
                usersButtons[i].setFont(new Font("ITALIC", Font.BOLD, 26));
                usersButtons[i].setIcon(userIcon.get(i));
                usersButtons[i].setLocation(10,i*70);
                usersButtons[i].addActionListener(this);
                innerPanel.add(usersButtons[i]);

                JPanel onlineStatusPanel = new JPanel();
                onlineStatusPanel.setSize(309,20);
                if(onlineStatus[i]){
                    onlineStatusPanel.setBackground(Color.green);
                }
                else{
                    onlineStatusPanel.setBackground(Color.red);
                }
                onlineStatusPanel.setLocation(15,i*70);
                innerPanel.add(onlineStatusPanel);

            }
            else{
                //System.out.println("inside not a friend else");

                JPanel onlineStatusPanel = new JPanel();
                onlineStatusPanel.setSize(309,20);
                if(onlineStatus[i]){
                    usersButtons[i] = new JButton(userName.get(i));
                    usersButtons[i].setSize(320,60);
                    usersButtons[i].setBackground(Color.yellow);
                    usersButtons[i].setFont(new Font("ITALIC", Font.BOLD, 26));
                    usersButtons[i].setIcon(userIcon.get(i));
                    usersButtons[i].setLocation(10,i*70);
                    usersButtons[i].addActionListener(this);
                    innerPanel.add(usersButtons[i]);

                    onlineStatusPanel.setBackground(Color.green);
                    onlineStatusPanel.setLocation(15,i*70);
                    innerPanel.add(onlineStatusPanel);
                }else{

                }
            }
        }

        innerPanel.setPreferredSize(new Dimension(350, userName.size() * 70));

        innerPanel.revalidate();
        innerPanel.repaint();
    }

    /**
     * This method updates friend list.
     * @param userName username to change status for.
     * @param usersImage users iamge to change status for.
     */
    public void friendChanger(String userName, ImageIcon usersImage){
        //System.out.println("changing friend status");
        for (int i = 0; i < usersButtons.length; i++) {
            if(Objects.equals(userName, usersButtons[i].getText())){
                if(isFriend[i]){
                    isFriend[i] = false;
                    friends.remove(i);
                    friendsIcon.remove(i);
                    for (int j = 0; j < isFriend.length; j++) {
                        isFriend[i] = isFriend[i+1];
                    }
                    boolean[] temporaryFriends = new boolean[isFriend.length-1];
                    System.arraycopy(isFriend,0,temporaryFriends,0,temporaryFriends.length);
                    isFriend = temporaryFriends;
                    //System.out.println("now u fuck yourself");
                    break;

                }
                else{
                    friends.add(userName);
                    friendsIcon.add(usersImage);
                    boolean[] temporaryFriends = new boolean[isFriend.length+2];
                    for (int j = 0; j < temporaryFriends.length; j++) {
                        temporaryFriends[i] = isFriend[i];
                    }
                    temporaryFriends[temporaryFriends.length-1] = true;
                    isFriend = temporaryFriends;
                    //System.out.println("now u friend");
                }
            }
        }
        clientGUI.refreshFriends(friends,friendsIcon);
    }

    /**
     * This method looks if selected user exist in friend list.
     * @param userName username to check friend status.
     * @return boolean, if user friend returns true, otherwise false.
     */
    public boolean checkFriend(String userName){
        for(String name : friends){
            if (Objects.equals(name, userName)){
                return true;
            }
        }
        return false;
    }

    /**
     * This method makes ability for buttons to functioning. It opens new chat with selected user or creates new chat.
     * @param e the event to be processed.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < usersButtons.length; i++) {
            if(e.getSource() == usersButtons[i]){
                if(clientGUI.chatExist(usersButtons[i].getText())){
                    clientGUI.openChat(usersButtons[i].getText());
                }
                else {
                    clientGUI.createChat(usersButtons[i].getText(),userIcon.get(i));
                    clientGUI.openChat(usersButtons[i].getText());
                }
            }
        }
    }
}
