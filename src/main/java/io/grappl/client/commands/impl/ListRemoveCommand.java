package io.grappl.client.commands.impl;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;
import io.grappl.client.commands.CommandHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintStream;

public class ListRemoveCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        try {
            ClientLog.log("Removing from server list");
            dataOutputStream.writeByte(7);
            PrintStream printStream = new PrintStream(dataOutputStream);
            printStream.println(CommandHandler.send);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
