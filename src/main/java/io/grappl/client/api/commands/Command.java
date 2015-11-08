package io.grappl.client.api.commands;

import io.grappl.client.api.Grappl;

public interface Command {

    public void runCommand(Grappl grappl, String[] args);
}
