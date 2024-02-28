package globalEntity;


import javax.swing.*;
import java.time.LocalDateTime;

public class ChatMessage extends Message {
    private String text;
    private ImageIcon image;
    private String receiver;
    private String sender;
    private LocalDateTime timeUserReceived;

    /**
     *
     * @param receiver
     * @param text
     * @param image
     */
    public ChatMessage(String receiver, String text, ImageIcon image, String sender) {
        super();
        this.receiver = receiver;
        this.text = text;
        this.image = image;
        this.sender = sender;
    }

    @Override
    public LocalDateTime getTimeServerReceived() {
        return super.getTimeServerReceived();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimeUserReceived() {
        return timeUserReceived;
    }

    public void setTimeUserReceived(LocalDateTime timeUserReceived) {
        this.timeUserReceived = timeUserReceived;
    }

    public ImageIcon getImage() {
        return image;
    }

    public void setImage(ImageIcon image) {
        this.image = image;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
