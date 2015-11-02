package io.grappl.client;

import io.grappl.client.commands.CommandHandler;
import io.grappl.client.log.ClientLog;
import io.grappl.client.log.ErrorStream;
import io.grappl.client.log.SilentClientLog;
import sun.applet.AppletListener;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Represents a single running instance of the Grappl application.
 * Not a tunnel, the entire program.
 */
public class Application {

    private static CommandHandler commandHandler;
    private static ClientLog clientLog;
    private static Mode mode;

    public static Mode getMode() {
        return mode;
    }

    public static void setMode(Mode mode) {
        Application.mode = mode;
    }

    public static void create(String[] args, Mode mode) {
        Application.mode = mode;
        clientLog = new ClientLog();
        commandHandler = new CommandHandler();

        System.setErr(new ErrorStream(System.out));
    }

    public static ClientLog getClientLog() {
        if(clientLog == null) {
            clientLog = new SilentClientLog();
        }

        return clientLog;
    }

    public static CommandHandler getCommandHandler() {
        if(commandHandler == null) {
            commandHandler = new CommandHandler();
        }

        return commandHandler;
    }
}
