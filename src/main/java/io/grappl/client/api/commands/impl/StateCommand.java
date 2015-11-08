package io.grappl.client.api.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.commands.Command;

/**
 * Displays the state of the current Grappl connection.
 */
public class StateCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        if(grappl == null) {
            Application.getLog().log(Application.NO_GRAPPL_MESSAGE);
        } else {
            Application.getLog().log(grappl.getRelayServer() + " relay server");
            Application.getLog().log(grappl.getExternalPort() + " external port");
            Application.getLog().log(grappl.getInternalAddress() + " internal server");
            Application.getLog().log(grappl.getInternalPort() + " internal port");
        }
    }
}
