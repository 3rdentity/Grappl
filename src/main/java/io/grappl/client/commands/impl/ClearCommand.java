package io.grappl.client.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.Mode;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;
import io.grappl.client.gui.ConsoleGUI;

public class ClearCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        // GUI mode
        if(Application.getMode() == Mode.NORMAL) {
            Application.getClientLog().clearVisibleLog();
            ConsoleGUI.logDisplay.setText("");
            Application.getClientLog().log("Cleared.");
        }

        // Command line mode
        else {
            Application.getClientLog().log("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
            Application.getClientLog().log("Can't really clear, so here's a space.");
        }
    }
}
