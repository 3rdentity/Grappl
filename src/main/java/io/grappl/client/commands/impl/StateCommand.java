package io.grappl.client.commands.impl;

import io.grappl.GrapplGlobals;
import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Displays the state of the current Grappl connection.
 */
public class StateCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        if(grappl == null) {
            ClientLog.log(GrapplGlobals.NO_GRAPPL_MESSAGE);
        } else {
            ClientLog.log(grappl.getRelayServer() + " relay server");
            ClientLog.log(grappl.getExternalPort() + " external port");
            ClientLog.log(grappl.getInternalAddress() + " internal server");
            ClientLog.log(grappl.getInternalPort() + " internal port");
        }
    }
}
