package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.impl.ApplicationState;
import io.grappl.client.impl.stable.StatMonitor;
import io.grappl.client.api.commands.Command;

public class StatsCommand implements Command {

    public static int avoided = 0;

    @Override
    public void runCommand(ApplicationState state, String[] args) {

        /* If grappl connection isn't open */
        if(state == null) {
            Application.getLog().log(Application.NO_GRAPPL_MESSAGE);
        }

        else {
            StatMonitor statsManager = state.getFocusedGrappl().getStatMonitor();

            Application.getLog().log("== CURRENT CONNECTION STATISTICS ====");
            Application.getLog().log(statsManager.getTotalConnections() + " clients connected");
            Application.getLog().log(statsManager.getReceivedData() + " bytes received");
            Application.getLog().log(statsManager.getSentData() + " bytes sent");
            Application.getLog().log(avoided + " bytes avoided");
            Application.getLog().log("=====================================");
        }
    }
}
