package io.grappl.client.commands.impl;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RelayCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        if(grappl == null) {
            ClientLog.log("No tunnel established, no relay");
        } else {
            ClientLog.log("Current relay: " + grappl.getRelayServer());
        }
    }
}
