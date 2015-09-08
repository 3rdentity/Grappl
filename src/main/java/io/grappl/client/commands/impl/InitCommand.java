package io.grappl.client.commands.impl;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.GrapplBuilder;
import io.grappl.client.commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class InitCommand implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {

        grappl = new GrapplBuilder().build();
        ClientLog.log("Starting...");
        grappl.connect(args[1]);
    }
}
