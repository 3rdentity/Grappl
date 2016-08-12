package io.grappl.client.impl.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

import java.io.PrintStream;

public class IpBanCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {
        if (state != null) {
            if (state.getAuthentication().isLoggedIn()) {
                try {
                    String ipToBAN = args[1];
                    state.getAuthentication().banIP(args[1]);
                    Application.getLog().log("Banned ip: " + ipToBAN);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Application.getLog().log("You must be logged in to ban IPs.");
            }
        } else {
            Application.getLog().log(Application.NO_GRAPPL_MESSAGE);
        }
    }

    @Override
    public String getDescription() {
        return "Ban an IP from connecting to a user's servers at the account level";
    }
}
