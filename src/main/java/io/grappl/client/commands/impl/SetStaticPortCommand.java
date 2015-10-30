package io.grappl.client.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

public class SetStaticPortCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {
        if (grappl.getAuthentication().isLoggedIn()) {
            try {
                if (grappl.getAuthentication().isPremium()) {
                    grappl.getAuthentication().getAuthDataOutputStream().writeByte(2);
                    grappl.getAuthentication().getAuthDataOutputStream().writeInt(Integer.parseInt(args[1]));
                    Application.getClientLog().log("Your port was set to: " + Integer.parseInt(args[1]));
                } else {
                    Application.getClientLog().log("You are not an alpha tester, so you can't set static ports.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Application.getClientLog().log("You are not logged in.");
        }
    }
}
