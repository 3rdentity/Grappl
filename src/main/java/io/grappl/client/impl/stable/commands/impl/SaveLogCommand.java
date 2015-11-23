package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

public class SaveLogCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {
        Application.getLog().log("Saving log...");
        Application.getLog().save();
        Application.getLog().log("Log saved");
    }

    @Override
    public String getDescription() {
        return "Saves the current console text-log to the filesystem.";
    }
}
