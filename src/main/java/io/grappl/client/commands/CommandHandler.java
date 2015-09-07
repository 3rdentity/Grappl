package io.grappl.client.commands;

import io.grappl.GrapplGlobal;
import com.daexsys.grappl.client.Client;
import io.grappl.client.ClientLog;
import io.grappl.client.GrapplClientState;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.impl.*;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandHandler {
    public static String send = "";

    public static Map<String, Command> commandMap = new HashMap<String, Command>();

    static {
        commandMap.put("disconnect", new DisconnectCommand());
        commandMap.put("savelog", new SaveLogCommand());
        commandMap.put("relay", new RelayCommand());
        commandMap.put("version", new VersionCommand());
        commandMap.put("ipban", new IpBanCommand());
        commandMap.put("ipunban", new IpUnban());
        commandMap.put("help", new HelpCommand());
        commandMap.put("init", new InitCommand());
        commandMap.put("whoami", new WhoAmICommand());
        commandMap.put("listadd", new ListAddCommand());
        commandMap.put("listremove", new ListRemoveCommand());

        commandMap.put("setstaticport", new SetStaticPortCommand());
        commandMap.put("setport", new SetStaticPortCommand());
    }

    public static void handleCommand(Grappl grappl, String command, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {

        String[] spl = command.split("\\s+");
        try {

            if(spl[0].equalsIgnoreCase("changelocal")) {
                grappl.setInternalPort(Integer.parseInt(spl[1]));
                ClientLog.log("Local port is now " + grappl.getInternalPort());
            }

            else if(spl[0].equalsIgnoreCase("changeserver")) {
                String server = spl[1];
                grappl.setInternalAddress(server);
                ClientLog.log("Server is now " + server);
            }

            else if(spl[0].equalsIgnoreCase("audible")) {
                GrapplClientState.audible = true;
            }

            else if(spl[0].equalsIgnoreCase("resetstats")) {
                grappl.getStatsManager().reset();
            }

            else if (commandMap.containsKey(spl[0].toLowerCase())) {
                commandMap.get(spl[0].toLowerCase()).runCommand(grappl, spl, dataInputStream, dataOutputStream);
            }

            else if (spl[0].equalsIgnoreCase("quit")) {
                System.exit(0);
            }

            else {
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

//            else if(spl[0].equalsIgnoreCase("freezer")) {
//                grappl.createFreezer();
//            }

//            else if(spl[0].equalsIgnoreCase("savefreezer")) {
//                grappl.getFreezer().save();
//            }
}
