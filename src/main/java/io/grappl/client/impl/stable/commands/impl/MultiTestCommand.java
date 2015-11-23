package io.grappl.client.impl.stable.commands.impl;

import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;
import io.grappl.client.impl.gui.multi.MultiLaunchGUI;

public class MultiTestCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {
        MultiLaunchGUI multiLaunchGUI = new MultiLaunchGUI();
        multiLaunchGUI.createWindow();
    }
}
