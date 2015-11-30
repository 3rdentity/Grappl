package io.grappl.client.impl.relay;

import io.grappl.client.impl.Application;
import io.grappl.client.impl.gui.AdvancedGUI;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RelayServer {

    private String relayLocation;
    private String description;

    private transient long ping = Long.MAX_VALUE;
    private boolean up = false;

    public RelayServer(String location, String description) {
        this.relayLocation = location;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isUp() {
        return up;
    }

    public long getLatency() {
        return ping;
    }

    public void ping() {
        if(AdvancedGUI.relayServerDropdown == null) {
            AdvancedGUI.relayServerDropdown = new JComboBox<String>();
        }

        long before = System.currentTimeMillis();

        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(getRelayLocation(), Application.MESSAGING_PORT), 300);
            socket.close();
        } catch (Exception e) {
            up = false;

            AdvancedGUI.relayServerDropdown.addItem(getRelayLocation()
                    + " (" + getDescription() + ") "
                    + getLatencyMessage());
            return;
        }
        ping = System.currentTimeMillis() - before;
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

        up = true;
    }

    public String getRelayLocation() {
        return relayLocation;
    }

    public String getLatencyMessage() {
        if(getLatency() < Integer.MAX_VALUE)
            return getLatency() + "ms";

        else return "UNREACHABLE";
    }
}
