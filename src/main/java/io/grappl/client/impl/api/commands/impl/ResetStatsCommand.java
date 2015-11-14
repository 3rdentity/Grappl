package io.grappl.client.impl.api.commands.impl;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;

public class ResetStatsCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {
        grappl.getStatMonitor().reset();
        Application.getLog().log("Reset stats");
    }
}
