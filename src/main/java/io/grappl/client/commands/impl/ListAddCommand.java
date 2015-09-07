package io.grappl.client.commands.impl;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintStream;

public class ListAddCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        String send;

        try {
            ClientLog.log("Adding to server list");
            dataOutputStream.writeByte(6);
            String game = args[1];
            PrintStream printStream = new PrintStream(dataOutputStream);
            send = game + " - " + grappl.getRelayServer() + ":" + grappl.getExternalPort();
            printStream.println(send);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
