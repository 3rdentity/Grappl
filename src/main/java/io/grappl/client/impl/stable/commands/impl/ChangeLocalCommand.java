package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

public class ChangeLocalCommand implements Command {
    @Override
    public void runCommand(ApplicationState state, String[] args) {

        if(state == null) {
            Application.getLog().log("Grappl has not been created");
        } else {
            state.getFocusedGrappl().setInternalPort(Integer.parseInt(args[1]));
            Application.getLog().log("Local port is now " + state.getFocusedGrappl().getInternalPort());
        }
    }
}
