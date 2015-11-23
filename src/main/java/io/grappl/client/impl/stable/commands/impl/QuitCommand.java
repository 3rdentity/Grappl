package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

/**
 * Ends the Grappl process cleanly
 */
public class QuitCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {
        Application.getLog().log("Saving log...");
        Application.getLog().save();
        Application.getLog().log("Saved log");
        Application.getLog().log("Stopping");
        System.exit(0);
    }

    @Override
    public String getDescription() {
        return "Closes the application.";
    }
}
