package io.grappl.client.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

public class ResetStatsCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {
        grappl.getStatMonitor().reset();
        Application.getClientLog().log("Reset stats");
    }
}
