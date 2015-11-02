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
    private List<Command> commands = new ArrayList<Command>();
    private PriorityQueue<String> commandsAlphabetized = new PriorityQueue<String>();

    @SuppressWarnings("SpellCheckingInspection")
    public CommandHandler() {
        /* Add default commands */
        addCommand("help", new HelpCommand());
        addCommand("version", new VersionCommand());
        addCommand("quit", new QuitCommand());

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
        String[] args = command.split("\\s+");

        if (commandMap.containsKey(args[0].toLowerCase())) {
            commandMap.get(args[0].toLowerCase()).runCommand(grappl, args);
        }
        else {
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

                            if (commandMap.containsKey(args[0].toLowerCase())) {
                                commandMap.get(args[0].toLowerCase()).runCommand(grappl, args);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }
            });

            commandThread.setName("Grappl Console Command Thread");
            commandThread.start();
            commandThreadStarted = true;
        }
    }

    public PriorityQueue<String> getCommandsAlphabetized() {
        return commandsAlphabetized;
    }
}
