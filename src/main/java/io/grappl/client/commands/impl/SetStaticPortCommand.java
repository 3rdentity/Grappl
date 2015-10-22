package io.grappl.client.commands.impl;

import io.grappl.client.ClientLog;
import io.grappl.client.api.Grappl;
import io.grappl.client.commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class SetStaticPortCommand implements Command {
    @Override
    public void runCommand(Grappl grappl, String[] args, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        if (grappl.getAuthentication().isLoggedIn()) {
            try {
                if (grappl.getAuthentication().isPremium()) {
                    dataOutputStream.writeByte(2);
                    dataOutputStream.writeInt(Integer.parseInt(args[1]));
                    ClientLog.log("Your port was set to: " + Integer.parseInt(args[1]));
                } else {
                    ClientLog.log("You are not an alpha tester, so you can't set static ports.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ClientLog.log("You are not logged in.");
        }
    }
}
