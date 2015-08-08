package com.daexsys.grappl.client;

import io.grappl.GrapplGlobal;
import io.grappl.client.gui.GrapplGUI;
import io.grappl.client.commands.CommandHandler;

import java.io.*;
import java.net.Socket;

public class Client {
    public int localPort = 0;

    // Socket related
    public static Socket authSocket;

    public static void main(String[] args) {
        Client client = new Client();
        client.start(args);
    }

    public void start(String args[]) {

        boolean displayGui = true;

        // Handle command line arguments
        if(args.length > 1) {
            if (args[0].equalsIgnoreCase("nogui")) {
                displayGui = false;
            }

            localPort = Integer.parseInt(args[1]);
        }

        // If there should be a gui, create it
        if(displayGui) {
            new GrapplGUI();
        }

        // Open connection to auth server (@ grappl.io)
        try {
            authSocket = new Socket(GrapplGlobal.DOMAIN, GrapplGlobal.AUTHENTICATION);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Start command line command handling thread
        CommandHandler.createCommandThread();
    }
}
