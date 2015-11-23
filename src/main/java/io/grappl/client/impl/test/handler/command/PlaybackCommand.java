package io.grappl.client.impl.test.handler.command;

import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

public class PlaybackCommand implements Command {
    @Override
    public void runCommand(ApplicationState state, String[] args) {
    }

    @Override
    public String getDescription() {
        return null;
    }
}
