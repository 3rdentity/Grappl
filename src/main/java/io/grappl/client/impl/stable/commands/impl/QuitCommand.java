package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;

/**
 * Ends the Grappl process cleanly
 */
public class QuitCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        Application.getLog().log("Saving log...");
        Application.getLog().save();
        Application.getLog().log("Saved log");
        Application.getLog().log("Stopping");
        System.exit(0);
    }
}
