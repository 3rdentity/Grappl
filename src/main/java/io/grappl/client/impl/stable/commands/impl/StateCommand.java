package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;
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
            Application.getLog().log(grappl.getExternalServer().getAddress() + " relay server");
            Application.getLog().log(grappl.getExternalServer().getPort() + " external port");
            Application.getLog().log(grappl.getInternalAddress() + " internal server");
            Application.getLog().log(grappl.getInternalPort() + " internal port");
        }
    }
}
