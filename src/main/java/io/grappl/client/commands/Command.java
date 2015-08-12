package io.grappl.client.commands;

import io.grappl.client.api.Grappl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public interface Command {

    public void runCommand(Grappl grappl, DataInputStream dataInputStream, DataOutputStream dataOutputStream);
}
