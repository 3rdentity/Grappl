package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;
import io.grappl.client.impl.stable.GrapplBuilder;
import io.grappl.client.api.commands.Command;

public class InitCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        String relay = "n.grappl.io";

        if(args.length == 2) {
            relay = args[1];
        }

        grappl = new GrapplBuilder().build();
        Application.getLog().log("Starting...");
        grappl.connect(relay);
    }
}
