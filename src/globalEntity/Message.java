package globalEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class Message implements Serializable {
    private LocalDateTime timeServerReceived;

    public Message() {
        timeServerReceived = LocalDateTime.now();
    }

    public LocalDateTime getTimeServerReceived() {
        return timeServerReceived;
    }
}
