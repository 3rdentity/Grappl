package io.grappl.client.commands.impl;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

/**
 * Ends the Grappl process cleanly
 */
public class QuitCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        ClientLog.log("Saving log...");
        ClientLog.save();
        ClientLog.log("Saved log");
        ClientLog.log("Stopping");
        System.exit(0);
    }
}
