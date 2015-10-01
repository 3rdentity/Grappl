package io.grappl.client.commands.impl;

import io.grappl.GrapplGlobals;
import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.StatsMonitor;
import io.grappl.client.commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class StatsCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {

        /* If grappl connection isn't open */
        if(grappl == null) {
            ClientLog.log(GrapplGlobals.NO_GRAPPL_MESSAGE);
        }

        else {
            StatsMonitor statsManager = grappl.getStatsManager();

            ClientLog.log("== CURRENT CONNECTION STATISTICS ====");
            ClientLog.log(statsManager.getReceivedData() + " bytes received");
            ClientLog.log(statsManager.getSentData() + " bytes sent");
            ClientLog.log("=====================================");
        }
    }
}
