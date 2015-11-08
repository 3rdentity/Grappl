package io.grappl.client.api.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.ApplicationMode;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.commands.Command;
import io.grappl.client.gui.ConsoleGUI;

public class ClearCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        // GUI mode
        if(Application.getApplicationMode() == ApplicationMode.NORMAL) {
            Application.getLog().clearVisibleLog();
            ConsoleGUI.logDisplay.setText("");
            Application.getLog().log("Cleared.");
        }

        // Command line mode
        else {
            Application.getLog().log("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
            Application.getLog().log("Can't really clear, so here's a space.");
        }
    }
}
