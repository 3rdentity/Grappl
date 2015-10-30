package io.grappl.client.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

public class ChangeLocalCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {

        if(grappl == null) {
            Application.getClientLog().log("Grappl has not been created");
        } else {
            grappl.setInternalPort(Integer.parseInt(args[1]));
            Application.getClientLog().log("Local port is now " + grappl.getInternalPort());
        }
    }
}
