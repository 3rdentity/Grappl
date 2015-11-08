package io.grappl.client.api.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.commands.Command;

public class SaveLogCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        Application.getLog().log("Saving log...");
        Application.getLog().save();
        Application.getLog().log("Log saved");
    }
}
