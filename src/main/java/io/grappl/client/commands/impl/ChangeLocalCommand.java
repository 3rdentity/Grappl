package io.grappl.client.commands.impl;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ChangeLocalCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args) {

        if(grappl == null) {
            ClientLog.log("Grappl has not been created");
        } else {
            grappl.setInternalPort(Integer.parseInt(args[1]));
            ClientLog.log("Local port is now " + grappl.getInternalPort());
        }
    }
}
