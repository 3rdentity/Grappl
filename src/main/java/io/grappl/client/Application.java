package io.grappl.client;

import io.grappl.client.commands.CommandHandler;

/**
 * Represents a single running instance of the Grappl application.
 * Not a tunnel, the entire program.
 */
public class Application {

    private static CommandHandler commandHandler;

    public static void create(String[] args) {
        commandHandler = new CommandHandler();
    }

    public static CommandHandler getCommandHandler() {
        return commandHandler;
    }
}
