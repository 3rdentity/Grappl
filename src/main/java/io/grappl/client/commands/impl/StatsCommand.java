package io.grappl.client.commands.impl;

import io.grappl.GrapplGlobals;
import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.StatMonitor;
import io.grappl.client.commands.Command;

public class StatsCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {

        /* If grappl connection isn't open */
        if(grappl == null) {
            Application.getClientLog().log(GrapplGlobals.NO_GRAPPL_MESSAGE);
        }

        else {
            StatMonitor statsManager = grappl.getStatMonitor();

            Application.getClientLog().log("== CURRENT CONNECTION STATISTICS ====");
            Application.getClientLog().log(statsManager.getTotalConnections() + " clients connected");
            Application.getClientLog().log(statsManager.getReceivedData() + " bytes received");
            Application.getClientLog().log(statsManager.getSentData() + " bytes sent");
            Application.getClientLog().log("=====================================");
        }
    }
}
