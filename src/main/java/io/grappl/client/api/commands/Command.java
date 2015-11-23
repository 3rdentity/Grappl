package io.grappl.client.api.commands;

import io.grappl.client.impl.ApplicationState;

public interface Command {

    public void runCommand(ApplicationState state, String[] args);
}
