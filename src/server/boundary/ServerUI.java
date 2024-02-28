package server.boundary;

import server.controller.ServerController;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * this class is resposible for the servers UI, in this implementation is only displays logs from a time span
 * and exit and closes but have implementation for expansion
 */
public class ServerUI extends Thread {

    private ServerController controller;
    private Scanner scanner;
    private int option;

    /**
     * the constructor is the main UI method and asks the user what they want to do in the program.
     * @param controller controller is saved as an instance in the
     * UI for when it wants to call for methods in the controller
     */
    public ServerUI(ServerController controller){
        scanner = new Scanner(System.in);
        this.controller = controller;

        while (true){
            System.out.println("What do you want to do?");
            System.out.println("1: display logs");
            System.out.println("2: save and exit");
            option = getInt(1,2);

            switch (option) {
                case 1 -> displayLogs();
                case 2 -> controller.shutdown();
                default -> System.out.println("Invalid option");
            }
        }
    }

    /**
     * the display logs method asks the user to enter a beginTime and EndTime and sends that to the controller and gets
     * backlog Strings and displays it
     */
    private void displayLogs() {
        YearMonth selectedYear;

        System.out.println("From what time do you want to get logs?");
        System.out.println("Enter Year[yyyy]");
        int year = getInt(0, LocalDateTime.now().getYear());
        System.out.println("Enter month[mm]");
        int month = getInt(1,12);
        selectedYear = YearMonth.of(year,month);
        System.out.println("Enter day[dd]");
        int day = getInt(1,selectedYear.lengthOfMonth());
        System.out.println("Enter hour[hh]");
        int hour = getInt(0,23);
        System.out.println("Enter minute[mm]");
        int minute = getInt(0,59);

        LocalDateTime beginTime = LocalDateTime.of(year,month,day,hour,minute);

        System.out.println("To what time do you want to get logs?");
        System.out.println("Enter Year[yyyy]");
        year = getInt(0, LocalDateTime.now().getYear());
        System.out.println("Enter month[mm]");
        month = getInt(1,12);
        selectedYear = YearMonth.of(year,month);
        System.out.println("Enter day[dd]");
        day = getInt(1,selectedYear.lengthOfMonth());
        System.out.println("Enter hour[hh]");
        hour = getInt(0,23);
        System.out.println("Enter minute[mm]");
        minute = getInt(0,59);

        LocalDateTime endTime = LocalDateTime.of(year,month,day,hour,minute);

        ArrayList<String> logs = controller.getLogs(beginTime,endTime);

        displayMessages(logs);

    }

    /**
     * getInt tells the user to enter a number in the console
     * @param low low is the lowest number the user is allowed to enter
     * @param high high is the highest number the user is allowed to enter
     * @return returns the integer that the user enters the console
     */
    private int getInt(int low, int high) {
        boolean gotValidNumber = false;
        int returnValue = low;
        while(!gotValidNumber){
            try{
                System.out.println(String.format("Enter a number between %s-%s",low,high));
                returnValue = Integer.parseInt(scanner.nextLine());
                if(returnValue <= high && returnValue >= low){
                    gotValidNumber = true;
                }
                else System.out.println("integer out of range");
            }
            catch(Exception e){
                System.out.println("enter a valid number");
            }
        }
        return returnValue;
    }

    /**
     * displays Strings from a string array given
     * @param messages string Array
     */
    public void displayMessages(ArrayList<String> messages) {
        for (int i = 0; i < messages.size(); i++){
            System.out.println(messages.get(i));
        }
    }
}
