package io.grappl.client.impl.relay;

import io.grappl.client.impl.Application;
import io.grappl.client.impl.gui.AdvancedGUI;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RelayServer {

    private final String relayLocation;
    private final String description;

    private transient long ping = Long.MAX_VALUE;

    private boolean relayIsOnline = false;

    public RelayServer(String location, String description) {
        this.relayLocation = location;
        this.description = description;
    }

    // TODO: need to remove GUI code from the logic
    public void ping() {
        if(AdvancedGUI.relayServerDropdown == null) {
            AdvancedGUI.relayServerDropdown = new JComboBox<String>();
        }

        /* START PING */
        long stopwatchStart = System.currentTimeMillis();

        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(getRelayLocation(), Application.MESSAGING_PORT), 300);
            socket.close();
        } catch (Exception e) {
            relayIsOnline = false;
            AdvancedGUI.relayServerDropdown.addItem(getRelayLocation()
                    + " (" + getDescription() + ") "
                    + getLatencyMessage());
            return;
        }
        ping = System.currentTimeMillis() - stopwatchStart;
        /* END PING */

        // Just to make sure
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AdvancedGUI.relayServerDropdown
                .setModel(new DefaultComboBoxModel<String>(Application.getApplicationState().getRelayManager().createList()));

        AdvancedGUI.relayServerDropdown.addItem(getRelayLocation()
                + " (" + getDescription() + ") "
                + getLatencyMessage());

        relayIsOnline = true;
    }

    public String getRelayLocation() {
        return relayLocation;
    }

    public long getLatency() {
        return ping;
    }

    public String getLatencyMessage() {
        if(getLatency() < Integer.MAX_VALUE)
            return getLatency() + "ms";

        else return "UNREACHABLE";
    }

    public boolean isUp() {
        return relayIsOnline;
    }

    public String getDescription() {
        return description;
    }
}
