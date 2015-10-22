package io.grappl.client.commands.impl;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintStream;

public class IpBanCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        if (grappl.getAuthentication().isLoggedIn()) {
            try {
                String ipToBAN = args[1];

                dataOutputStream.writeByte(5);
                PrintStream printStream = new PrintStream(dataOutputStream);
                printStream.println(ipToBAN);

                ClientLog.log("Banned ip: " + ipToBAN);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ClientLog.log("You must be logged in to ban IPs.");
        }
    }
}
