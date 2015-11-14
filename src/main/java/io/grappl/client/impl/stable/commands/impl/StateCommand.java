package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

/**
 * Displays the state of the current Grappl connection.
 */
public class StateCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {
        if(state == null) {
            Application.getLog().log(Application.NO_GRAPPL_MESSAGE);
        } else {
            Application.getLog().log(state.getFocusedGrappl().getExternalServer().getAddress() + " relay server");
            Application.getLog().log(state.getFocusedGrappl().getExternalServer().getPort() + " external port");
            Application.getLog().log(state.getFocusedGrappl().getInternalAddress() + " internal server");
            Application.getLog().log(state.getFocusedGrappl().getInternalPort() + " internal port");
        }
    }
}
