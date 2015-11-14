package io.grappl.client.api.commands;

import io.grappl.client.api.Grappl;

public interface Command {

    // TODO: Replace Grappl argument with ApplicationState argument
    public void runCommand(Grappl grappl, String[] args);
}
