package io.grappl.client.commands.impl;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ResetStatsCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {
        grappl.getStatMonitor().reset();
        ClientLog.log("Reset stats");
    }
}
