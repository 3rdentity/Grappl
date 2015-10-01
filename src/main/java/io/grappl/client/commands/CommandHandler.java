package io.grappl.client.commands;

import io.grappl.GrapplGlobal;
import com.daexsys.grappl.client.Client;
import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.impl.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
        commandMap.put("ipunban", new IpUnbanCommand());
        commandMap.put("help", new HelpCommand());
        commandMap.put("init", new InitCommand());
        commandMap.put("whoami", new WhoAmICommand());
        commandMap.put("listadd", new ListAddCommand());
        commandMap.put("listremove", new ListRemoveCommand());
        commandMap.put("changelocal", new ChangeLocalCommand());
        commandMap.put("changeserver", new ChangeServerCommand());
        commandMap.put("resetstats", new ResetStatsCommand());
        commandMap.put("quit", new QuitCommand());
        commandMap.put("state", new StateCommand());
        commandMap.put("account", new AccountCommand());
        commandMap.put("dummy", new DummyServer());
        commandMap.put("stats", new StatsCommand());

        commandMap.put("setstaticport", new SetStaticPortCommand());
        commandMap.put("setport", new SetStaticPortCommand());
    }

    public static void handleCommand(Grappl grappl, String command, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {

        // If no command was actually entered, return.
        if(command.isEmpty()) return;

        // Split command string into words. args[0] is the commands name, all other are args.
        String[] args = command.split("\\s+");

        if (commandMap.containsKey(args[0].toLowerCase())) {
            commandMap.get(args[0].toLowerCase()).runCommand(GrapplGlobal.theGrappl, args, dataInputStream, dataOutputStream);
        }

        else {
            ClientLog.log("Unknown command");
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

                while(scanner.hasNextLine()) {
                    try {
                        String line = scanner.nextLine();
                        String[] args = line.split("\\s+");

                        if (commandMap.containsKey(args[0].toLowerCase())) {
                            commandMap.get(args[0].toLowerCase()).runCommand(GrapplGlobal.theGrappl, args, dataInputStream, dataOutputStream);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        });

        commandThread.setName("Grappl Command Thread");
        commandThread.start();
    }
}
