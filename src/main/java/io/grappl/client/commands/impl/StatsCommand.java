package io.grappl.client.commands.impl;

import io.grappl.GrapplGlobals;
import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.StatMonitor;
import io.grappl.client.commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class StatsCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {

        /* If grappl connection isn't open */
        if(grappl == null) {
            ClientLog.log(GrapplGlobals.NO_GRAPPL_MESSAGE);
        }

        else {
            StatMonitor statsManager = grappl.getStatMonitor();

            ClientLog.log("== CURRENT CONNECTION STATISTICS ====");
            ClientLog.log(statsManager.getTotalConnections() + " clients connected");
            ClientLog.log(statsManager.getReceivedData() + " bytes received");
            ClientLog.log(statsManager.getSentData() + " bytes sent");
            ClientLog.log("=====================================");
        }
    }
}
