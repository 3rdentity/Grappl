package io.grappl.client.impl.api.commands.impl;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;
import io.grappl.client.impl.api.StatMonitor;
import io.grappl.client.api.commands.Command;

public class StatsCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {

        /* If grappl connection isn't open */
        if(grappl == null) {
            Application.getLog().log(Application.NO_GRAPPL_MESSAGE);
        }

        else {
            StatMonitor statsManager = grappl.getStatMonitor();

            Application.getLog().log("== CURRENT CONNECTION STATISTICS ====");
            Application.getLog().log(statsManager.getTotalConnections() + " clients connected");
            Application.getLog().log(statsManager.getReceivedData() + " bytes received");
            Application.getLog().log(statsManager.getSentData() + " bytes sent");
            Application.getLog().log("=====================================");
        }
    }
}
