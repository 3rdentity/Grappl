package io.grappl.client.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

public class SaveLogCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        Application.getClientLog().log("Saving log...");
        Application.getClientLog().save();
        Application.getClientLog().log("Log saved");
    }
}
