package io.grappl.client.commands.impl;

import io.grappl.GrapplGlobals;
import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

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

                    Application.getClientLog().log("Banned ip: " + ipToBAN);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Application.getClientLog().log("You must be logged in to ban IPs.");
            }
        } else {
            Application.getClientLog().log(GrapplGlobals.NO_GRAPPL_MESSAGE);
        }
    }
}
