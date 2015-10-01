package io.grappl.client.commands.impl;

import io.grappl.GrapplGlobals;
import io.grappl.client.ClientLog;
import io.grappl.client.GrapplClientState;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class VersionCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        ClientLog.log(GrapplGlobals.APP_NAME + " version " + GrapplClientState.VERSION);
    }
}
