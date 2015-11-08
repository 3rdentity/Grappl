package io.grappl.client.api.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.GrapplBuilder;
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
