package io.grappl.client.commands.impl;

import io.grappl.GrapplGlobals;
import io.grappl.client.Application;
import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

/**
 * Displays the state of the current Grappl connection.
 */
public class StateCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        if(grappl == null) {
            Application.getClientLog().log(GrapplGlobals.NO_GRAPPL_MESSAGE);
        } else {
            Application.getClientLog().log(grappl.getRelayServer() + " relay server");
            Application.getClientLog().log(grappl.getExternalPort() + " external port");
            Application.getClientLog().log(grappl.getInternalAddress() + " internal server");
            Application.getClientLog().log(grappl.getInternalPort() + " internal port");
        }
    }
}
