package client.controller;

import client.boundary.LoginClient;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
       new ClientController("localhost",420);
    }
}
