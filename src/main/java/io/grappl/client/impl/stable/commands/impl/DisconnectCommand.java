package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

public class DisconnectCommand implements Command {

    public void runCommand(ApplicationState state, String[] args) {
        if(state == null) {
            Application.getLog().log("Not connected, cannot disconnect");
        } else {
            Application.getLog().log("Disconnecting...");
            state.getFocusedGrappl().disconnect();
        }
    }
}
