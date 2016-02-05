package io.grappl.client.impl.commands.impl;

import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;
import io.grappl.gui.multi.MultiLaunchGUI;

public class MultiTestCommand implements Command {

    @Override
    public void runCommand(ApplicationState state, String[] args) {
        MultiLaunchGUI multiLaunchGUI = new MultiLaunchGUI();
        multiLaunchGUI.createWindow();
    }

    @Override
    public String getDescription() {
        return "Launches a test instance of the multiple-Grappl-GUI. Alpha as it gets.";
    }
}
