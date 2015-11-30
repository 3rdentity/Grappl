package io.grappl.client.impl.commands.impl;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

import java.io.PrintStream;

public class IpUnbanCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {

        if (state.getAuthentication().isLoggedIn()) {
            try {
                String ipToUnban = args[1];

                state.getAuthentication().getAuthDataOutputStream().writeByte(8);
                PrintStream printStream = new PrintStream(state.getAuthentication().getAuthDataOutputStream());
                printStream.println(ipToUnban);

                Application.getLog().log("Unbanned ip: " + ipToUnban);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Application.getLog().log("You must be logged in to unban IPs.");
        }
    }

    @Override
    public String getDescription() {
        return "Unban an IP from connecting to a user's servers at the account level";
    }
}
