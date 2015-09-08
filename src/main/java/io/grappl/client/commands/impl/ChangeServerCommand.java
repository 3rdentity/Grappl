package io.grappl.client.commands.impl;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ChangeServerCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        String server = args[1];
        grappl.setInternalAddress(server);
        ClientLog.log("Server is now " + server);
    }
}
