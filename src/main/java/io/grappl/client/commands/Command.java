package io.grappl.client.commands;

import io.grappl.client.api.Grappl;

import java.net.Socket;

public class Command {

    private String name;

    public Command(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void runCommand(Grappl grappl, Socket communicationSocket) {

    }
}
