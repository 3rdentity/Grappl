package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;

public class SetStaticPortCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {
        if (grappl.getAuthentication().isLoggedIn()) {
            try {
                if (grappl.getAuthentication().isPremium()) {
                    grappl.getAuthentication().getAuthDataOutputStream().writeByte(2);
                    grappl.getAuthentication().getAuthDataOutputStream().writeInt(Integer.parseInt(args[1]));
                    Application.getLog().log("Your port was set to: " + Integer.parseInt(args[1]));
                } else {
                    Application.getLog().log("You are not an alpha tester, so you can't set static ports.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Application.getLog().log("You are not logged in.");
        }
    }
}
