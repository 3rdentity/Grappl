package io.grappl.client.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

/**
 * Ends the Grappl process cleanly
 */
public class QuitCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        Application.getClientLog().log("Saving log...");
        Application.getClientLog().save();
        Application.getClientLog().log("Saved log");
        Application.getClientLog().log("Stopping");
        System.exit(0);
    }
}
