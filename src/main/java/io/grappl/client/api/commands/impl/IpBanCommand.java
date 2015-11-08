package io.grappl.client.api.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.commands.Command;

import java.io.PrintStream;

public class IpBanCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {
        if (grappl != null) {
            if (grappl.getAuthentication().isLoggedIn()) {
                try {
                    String ipToBAN = args[1];

                    grappl.getAuthentication().getAuthDataOutputStream().writeByte(5);
                    PrintStream printStream = new PrintStream(grappl.getAuthentication().getAuthDataOutputStream());
                    printStream.println(ipToBAN);

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
}
