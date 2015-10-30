package io.grappl.client.commands;

import io.grappl.client.api.Grappl;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface Command {

    public void runCommand(Grappl grappl, String[] args);
}
