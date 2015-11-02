package io.grappl.client.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;
import io.grappl.client.gui.ConsoleGUI;

public class ClearCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        Application.getClientLog().clearVisibleLog();
        ConsoleGUI.logDisplay.setText("");
        Application.getClientLog().log("Cleared.");
    }
}
