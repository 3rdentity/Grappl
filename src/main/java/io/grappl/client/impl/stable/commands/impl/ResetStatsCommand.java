package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

public class ResetStatsCommand implements Command {
    @Override
    public void runCommand(ApplicationState state, String[] args) {
        state.getFocusedGrappl().getStatMonitor().reset();
        Application.getLog().log("Reset stats");
    }
}
