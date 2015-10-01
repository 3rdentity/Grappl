package io.grappl.client.commands.impl;

import io.grappl.GrapplGlobal;
import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.StatsManager;
import io.grappl.client.commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class StatsCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {

        if(grappl == null) {
            ClientLog.log(GrapplGlobal.NO_GRAPPL_MESSAGE);
        } else {
            StatsManager statsManager = grappl.getStatsManager();

            ClientLog.log("== STATISTICS ====");
            ClientLog.log(statsManager.getReceivedData() + " bytes received");
            ClientLog.log(statsManager.getSentData() + " bytes sent");
            ClientLog.log("==================");
        }
    }
}
