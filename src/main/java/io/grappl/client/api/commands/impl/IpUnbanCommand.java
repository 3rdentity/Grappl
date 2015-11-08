package io.grappl.client.api.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.commands.Command;

import java.io.PrintStream;

public class IpUnbanCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {

        if (grappl.getAuthentication().isLoggedIn()) {
            try {
                String ipToUnban = args[1];

                grappl.getAuthentication().getAuthDataOutputStream().writeByte(8);
                PrintStream printStream = new PrintStream(grappl.getAuthentication().getAuthDataOutputStream());
                printStream.println(ipToUnban);

                Application.getLog().log("Unbanned ip: " + ipToUnban);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Application.getLog().log("You must be logged in to unban IPs.");
        }
    }
}
