package server.entity;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * the log handler class handles the logs and is responsible for storing them in the system
 */
public class LogHandler {
    private ArrayList<Log> logs;

    /**
     * The constructor of the loghandler class calls the openLogs() method to get all the saved logs from the system.
     */
    public LogHandler() {
        logs = openLogs();
    }

    /**
     * The getLogs command is used to get logs from a specified timespan. Before gathering the logs it sorts itself.
     * @param beginTime beginTime is the time from where you want to get the first log to formatted [yyyy:mm:dd:hh:mm]
     * @param endTime endTime is the time for where you dont want more logs from formatted [yyyy:mm:dd:hh:mm]
     * @return returns an ArrayList with logs at an unspecified amount, if no logs are found an array with a
     * single log is sent tellings the system that no logs are in the timespan
     */
    public ArrayList<Log> getLogs(LocalDateTime beginTime, LocalDateTime endTime) {
        ArrayList<Log> logsToSend = new ArrayList<>();

        //if beginTime specified is after the endTime a single log is sent telling that
        if(beginTime.isAfter(endTime)){
            ArrayList<Log> beginAfterEnd = new ArrayList<>();
            Log errorLog = new Log("before time is after end time");
            beginAfterEnd.add(errorLog);
            return beginAfterEnd;
        }

        //sort the loglist (incase logs are added the same time )
        Collections.sort(logs, new Comparator<Log>() {
            @Override
            public int compare(Log log1, Log log2) {
                return log1.getLocalDateTime().compareTo(log2.getLocalDateTime());
            }
        });
        //find which is the first log to take from selected time.
        boolean startpointFound = false;
        int low = 0;
        int high = logs.size();
        int index = 0;

        for (int i = 0; i < logs.size(); i++){
            logs.get(i).toString();
        }
        while (!startpointFound) {
            index = low + (high-low)/2;
            if (beginTime.isAfter(logs.get(index).getLocalDateTime())) {
                if(beginTime.isBefore(logs.get(index+1).getLocalDateTime())){
                    startpointFound = true;
                }
                else{
                    low = index;
                }
            }
            else {
                high = index;
            }
            //if high reaches 0 index will stay at 0, meaning the start point should be 0;
            if(index == 0) startpointFound = true;
        }
        //transfer all logs from the while-loop above until the logs are out of range
        boolean lastLogFound = false;
        while(!lastLogFound){
            try{
                if(endTime.isAfter(logs.get(index).getLocalDateTime())){
                    logsToSend.add(logs.get(index));
                }
                else lastLogFound = true;

            //this statement is called when out of bounds is reached, meaning the end date is after the latest log.
            }catch (Exception e){
                lastLogFound = true;
            }
            index++;
        }

        //if there is no logs to retrieve between the timespan specified then a single log is added to the array with a message explaing that
        if(logsToSend.size() == 0){
            logsToSend.add(new Log("There is no logs to display between that timespan"));
        }
        return logsToSend;
    }

    /**
     * returns all the logs.toString methods (never implemented)
     * @return returns an ArrayList with String from all the logs
     */
    public ArrayList<String> getLogStrings() {
        ArrayList<String> returnString = new ArrayList<String>();
        for (int i = 0; i < logs.size(); i++) returnString.add(logs.get(i).toString());
        return returnString;
    }

    /**
     * adds a log to the system based on the type of log which is speicified
     * @param text takes an array of strings based on what type of logs is specified, how and what
     *            strings needed are included in the comments of the LogType enum
     * @param logType LogType enum decides the formatting of the string varaible in the saved log.
     */
    public void addLog(String[] text, LogType logType) {
        switch (logType){
            case UserConnected -> logs.add(new Log(String.format("User [%s] connected to the server", text[0])));
            case UserDisconnected -> logs.add(new Log(String.format("User [%s] disconnected from the server", text[0])));
            case MessageSent -> logs.add(new Log(String.format("User [%s] sent a message to [%s]", text[0],text[1])));
            case MessageReceived -> logs.add(new Log(String.format("User [%s] received a message from user[%s]", text[0],text[1])));
            case UserRegistered -> logs.add(new Log(String.format("New user registered with id [%s]", text[0])));
            case TestLog -> logs.add(new Log(text[0]));
        }
    }

    /**
     * Saves logs in current instance to disk
     */
    public void saveLogs() {
        //System.out.println("saveLogs called");
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("src/resources/serverlog.dat"))) {
            System.out.println(logs);
            objectOutputStream.writeObject(logs);
        } catch (IOException e) {
            //System.out.println("LogHandler.java: Could not save logList");
        }
    }

    /**
     * Fetches saved logs from disk. Returns empty list if no file exists
     */
    public ArrayList<Log> openLogs() {
        //System.out.println("open logs called");
        File saveFile = new File("src/resources/"+"serverlog"+".dat");
        if (saveFile.exists() && saveFile.isFile()) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(saveFile))) {
                ArrayList<Log> logList = (ArrayList<Log>) objectInputStream.readObject();
                if (logList != null)
                    if (logList.size() > 0)
                        return logList;
            } catch (IOException e) {
                //System.out.println("LogHandler.java: Could not load ArrayList from disk: IOException");
            } catch (ClassNotFoundException e) {
                //System.out.println("LogHandler.java: Could not load ArrayList from disk: ClassNotFoundException");
            }
        }
        return new ArrayList<>();
    }

}
