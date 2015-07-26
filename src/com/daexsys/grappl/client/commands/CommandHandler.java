package com.daexsys.grappl.client.commands;

import com.daexsys.grappl.GrapplGlobal;
import com.daexsys.grappl.GrapplServerState;
import com.daexsys.grappl.client.Client;
import com.daexsys.grappl.client.ClientLog;
import com.daexsys.grappl.client.ConsoleWindow;
import com.daexsys.grappl.client.GrapplClientState;
import com.daexsys.grappl.client.api.Grappl;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class CommandHandler {
    public static String send = "";

    public static void handleCommand(Grappl grappl, String command, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        String ip = GrapplGlobal.DOMAIN;

        String[] spl = command.split("\\s+");
        try {

            if (spl[0].equalsIgnoreCase("ipban")) {

                if (grappl.isLoggedIn()) {
                    String ipToBAN = spl[1];

                    dataOutputStream.writeByte(5);
                    PrintStream printStream = new PrintStream(dataOutputStream);
                    printStream.println(ipToBAN);

                    ClientLog.log("Banned ip: " + ipToBAN);
                } else {
                    ClientLog.log("You must be logged in to ban IPs.");
                }
            }

            else if (spl[0].equalsIgnoreCase("ipunban")) {

                if (grappl.isLoggedIn()) {
                    String ipToBAN = spl[1];

                    dataOutputStream.writeByte(8);
                    PrintStream printStream = new PrintStream(dataOutputStream);
                    printStream.println(ipToBAN);

                    ClientLog.log("Unbanned ip: " + ipToBAN);
                } else {
                    ClientLog.log("You must be logged in to unban IPs.");
                }
            }


            else if (spl[0].equalsIgnoreCase("login")) {
                String username = spl[1];
                String password = spl[2];

                dataOutputStream.writeByte(0);

                PrintStream printStream = new PrintStream(dataOutputStream);
                printStream.println(username);
                printStream.println(password);

                boolean success = dataInputStream.readBoolean();
                boolean alpha = dataInputStream.readBoolean();
                int thePort = dataInputStream.readInt();
                grappl.setAlphaTester(alpha);
                grappl.setLoggedIn(success);

                if (success) {
                    ClientLog.log("Logged in as " + username);
                    ClientLog.log("Alpha tester: " + alpha);
                    ClientLog.log("Static port: " + thePort);
//                    Client.username = username;
                } else {
//                    JOptionPane.showMessageDialog(Client.grapplGUI.getjFrame(), "Login failed!");
                }
            } else if (spl[0].equalsIgnoreCase("whoami")) {
                if (grappl.isLoggedIn()) {
                    ClientLog.log(grappl.getUsername());
                } else {
                    ClientLog.log("You aren't logged in, so you are anonymous.");
                }
            }

            else if(spl[0].equalsIgnoreCase("relay")) {
                ClientLog.log(grappl.getUsername());
            }

            else if(spl[0].equalsIgnoreCase("version")) {
                ClientLog.log(GrapplGlobal.APP_NAME + " version " + GrapplClientState.VERSION);
            }

            else if(spl[0].equalsIgnoreCase("savelog")) {
                ClientLog.save();
                ClientLog.log("Log saved");
            }

            else if(spl[0].equalsIgnoreCase("listadd")) {
                ClientLog.log("Adding to server list");
                dataOutputStream.writeByte(6);
                String game = spl[1];
                PrintStream printStream = new PrintStream(dataOutputStream);
                send = game + " - " + grappl.getRelayServer() + ":"+ grappl.getExternalPort();
                printStream.println(send);
            }

            else if(spl[0].equalsIgnoreCase("listremove")) {
                ClientLog.log("Removing from server list");
                dataOutputStream.writeByte(7);
                PrintStream printStream = new PrintStream(dataOutputStream);
                printStream.println(send);
            }

            else if(spl[0].equalsIgnoreCase("changelocal")) {
                grappl.setInternalPort(Integer.parseInt(spl[1]));
                ClientLog.log("Local port is now " + grappl.getInternalPort());
            }

            else if(spl[0].equalsIgnoreCase("resetstats")) {
                grappl.getStatsManager().reset();
            }

            else if(spl[0].equalsIgnoreCase("help")) {
                ClientLog.log("COMMANDS: init, login [username] [password], setport [port]");
                ClientLog.log("listadd [gamename], listremovem, whoami, version, relay");
                ClientLog.log("changelocal [port], resetstats, ipban [ip], ipunban [ip], savelog");
            }

            else if (spl[0].equalsIgnoreCase("setport")) {
                if (grappl.isLoggedIn()) {
                    if (grappl.isAlphaTester()) {
                        dataOutputStream.writeByte(2);
                        dataOutputStream.writeInt(Integer.parseInt(spl[1]));
                        ClientLog.log("Your port was set to: " + Integer.parseInt(spl[1]));
                    } else {
                        ClientLog.log("You are not an alpha tester, so you can't set static ports.");
                    }
                } else {
                    ClientLog.log("You are not logged in.");
                }
            } else if (spl[0].equalsIgnoreCase("init")) {
                ClientLog.log("Starting...");
                grappl.connect(ip);

            } else if (spl[0].equalsIgnoreCase("quit")) {
                System.exit(0);
            } else {
                ClientLog.log("Unknown command");
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createCommandThread() {
        Thread commandThread = new Thread(new Runnable() {
            @Override
            public void run() {
                DataInputStream dataInputStream = null;
                DataOutputStream dataOutputStream = null;

                try {
                    dataInputStream = new DataInputStream(Client.authSocket.getInputStream());
                    dataOutputStream = new DataOutputStream(Client.authSocket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ClientLog.log(GrapplGlobal.APP_NAME + " Command Line");

                Scanner scanner = new Scanner(System.in);

                while(true) {
                    try {
                        String line = scanner.nextLine();
//                        CommandHandler.handleCommand(line, dataInputStream, dataOutputStream, Client.localPort);
                    } catch (Exception e) {
                        e.printStackTrace();
//                        System.exit(0);
                        return;
                    }
                }
            }
        });

        commandThread.setName("Grappl Command Thread");
        commandThread.start();
    }
}
