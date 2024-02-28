package server.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * the log class is a class that stores information about a
 */
public class Log implements Serializable {
    private String text;
    private LocalDateTime time;

    public Log(String text) {
        this.text = text;
        time = LocalDateTime.now();
    }

    /**
     * returns the text of the log (never used)
     * @return returns a String.
     */
    public String getText() {
        return text;
    }

    /**
     * sets the text in the log (never used)
     * @param text takes a String and sets the text 1:1
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * returns the time the log is created
     * @return returns a LocalDateTime specified as LocalDateTime.now when the log instance is created.
     */
    public LocalDateTime getLocalDateTime() {
        return time;
    }

    /**
     * this method forces the timestamp to be changed of the log creation time (only for testing purposes and should not be implemented)
     * @param year int year formatted as [yyyy]
     * @param month int month formatted as [mm]
     * @param day int day formatted as [dd]
     * @param hour int hour formatted as [hh]
     * @param minute int minute formatted as [mm]
     */
    public void forceTimeStamp(int year, int month, int day, int hour, int minute) {time = LocalDateTime.of(year,month,day,hour,minute);}

    /**
     * returns a String for the log formatted with [time] log message
     * @return returns a single string
     */
    @Override
    public String toString(){
        return String.format("[%s] %s", time.toString(), text);
    }
}
