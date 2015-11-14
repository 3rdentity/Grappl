package io.grappl.client.api.commands;

import io.grappl.client.impl.ApplicationState;

public interface Command {

    // TODO: Replace Grappl argument with ApplicationState argument
    public void runCommand(ApplicationState state, String[] args);
}
