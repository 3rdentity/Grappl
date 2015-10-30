package io.grappl.client.commands.impl;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

public class SaveLogCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        ClientLog.log("Saving log...");
        ClientLog.save();
        ClientLog.log("Log saved");
    }
}
