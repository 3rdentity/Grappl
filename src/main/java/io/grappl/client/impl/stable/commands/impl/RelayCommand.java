package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

public class RelayCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {
        if(state == null) {
            Application.getLog().log("No tunnel established, no relay");
        } else {
            Application.getLog().log("Current relay: " + state.getFocusedGrappl().getExternalServer().getAddress());
        }
    }
}
