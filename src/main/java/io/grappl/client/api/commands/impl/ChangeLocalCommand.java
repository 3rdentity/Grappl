package io.grappl.client.api.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.commands.Command;

public class ChangeLocalCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {

        if(grappl == null) {
            Application.getLog().log("Grappl has not been created");
        } else {
            grappl.setInternalPort(Integer.parseInt(args[1]));
            Application.getLog().log("Local port is now " + grappl.getInternalPort());
        }
    }
}
