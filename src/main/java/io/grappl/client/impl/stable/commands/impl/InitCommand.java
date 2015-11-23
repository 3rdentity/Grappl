package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.impl.ApplicationState;
import io.grappl.client.impl.stable.GrapplBuilder;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.error.RelayServerNotFoundException;

public class InitCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {
        String relay = "localhost";

        if(args.length == 2) {
            relay = args[1];
        }

        state.addGrappl(new GrapplBuilder().build());

        Application.getLog().log("Starting...");
        try {
            state.getFocusedGrappl().connect(relay);
        } catch (RelayServerNotFoundException e) {
            e.printStackTrace();
        }
    }
}
