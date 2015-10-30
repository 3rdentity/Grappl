package io.grappl.client.commands.impl;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class WhoAmICommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {
        if(grappl == null) {
            ClientLog.log("Grappl has not been initialized");
        } else {
            if (grappl.getAuthentication().isLoggedIn()) {
                ClientLog.log("You are logged in as: " + grappl.getAuthentication().getUsername());
            } else {
                ClientLog.log("You are not logged in.");
            }
        }
    }
}
