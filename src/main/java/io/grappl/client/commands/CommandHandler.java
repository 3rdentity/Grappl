package io.grappl.client.commands;

import io.grappl.GrapplGlobals;
import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.impl.*;

import java.util.*;

public class CommandHandler {
    
    public String returnBuffer = "";

    private boolean commandThreadStarted = false;

    private Map<String, Command> commandMap = new HashMap<String, Command>();
    private PriorityQueue<String> commandsAlphabetized = new PriorityQueue<String>();

    @SuppressWarnings("SpellCheckingInspection")
    public CommandHandler() {
        /* Add default commands */
        addCommand("help", new HelpCommand());
        addCommand("version", new VersionCommand());
        addCommand("quit", new QuitCommand());
        addCommand("clear", new ClearCommand());

        addCommand("init", new InitCommand());
        addCommand("disconnect", new DisconnectCommand());
        addCommand("logout", new LogoutCommand());

        addCommand("whoami", new WhoAmICommand());
        addCommand("relay", new RelayCommand());
        addCommand("stats", new StatsCommand());
        addCommand("state", new StateCommand());

        addCommand("dummy", new DummyServer());

        addCommand("savelog", new SaveLogCommand());

        addCommand("ipban", new IpBanCommand());
        addCommand("ipunban", new IpUnbanCommand());

        addCommand("listadd", new ListAddCommand());
        addCommand("listremove", new ListRemoveCommand());
        addCommand("changelocal", new ChangeLocalCommand());
        addCommand("changeserver", new ChangeServerCommand());
        addCommand("resetstats", new ResetStatsCommand());
        addCommand("account", new AccountCommand());
        addCommand("setstaticport", new SetStaticPortCommand());
        addCommand("setport", new SetStaticPortCommand());
    }

    private void addCommand(String string, Command command) {
        commandMap.put(string, command);
        commandsAlphabetized.add(string);
    }

    public void handleCommand(Grappl grappl, String command) {

        // If no command was actually entered, return.
        if(command.isEmpty())
            return;

        // Split command string into words. args[0] is the commands name, all other are args.
        String[] words = command.split("\\s+");
        String commandName = words[0].toLowerCase();

//        String[] args = Arrays.copyOfRange(words, 1, words.length);

        if (commandMap.containsKey(commandName)) {
            getCommand(commandName).runCommand(grappl, words);
        } else {
            Application.getClientLog().log("Unknown command '" + command + "'");
        }
    }

    public void createConsoleCommandListenThread(final Grappl grappl) {

        if(!commandThreadStarted) {
            Thread commandThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    Application.getClientLog().log(GrapplGlobals.APP_NAME + " Command Line");

                    Scanner scanner = new Scanner(System.in);

                    while (scanner.hasNextLine()) {
                        try {
                            String line = scanner.nextLine();
                            String[] args = line.split("\\s+");

                            String commandName = args[0].toLowerCase();

                            if (commandMap.containsKey(commandName)) {
                                getCommand(commandName).runCommand(grappl, args);
                            } else {
                                Application.getClientLog().log("Unknown command '" + commandName + "'");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            commandThread.setName("Grappl Console Command Thread");
            commandThread.start();
            commandThreadStarted = true;
        }
    }

    public Command getCommand(String name) {
        return commandMap.get(name);
    }

    public PriorityQueue<String> getCommandsAlphabetized() {
        return commandsAlphabetized;
    }
}
