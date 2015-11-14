package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

public class ChangeServerCommand implements Command {
    @Override
    public void runCommand(ApplicationState state, String[] args) {
        String server = args[1];
        state.getFocusedGrappl().setInternalAddress(server);
        Application.getLog().log("Server is now " + server);
    }
}
