package io.grappl.client.impl.adaptive;

import java.net.Socket;

public class RelayServer {

    private String relayLocation;
    private long ping = Long.MAX_VALUE;
    private boolean up = false;

    public RelayServer(String location) {
        this.relayLocation = location;
    }

    public boolean isUp() {
        return up;
    }

    public long getLatency() {
        return ping;
    }

    public void ping() {
        long before = System.currentTimeMillis();
        try {
            Socket socket = new Socket(getRelayLocation(), 25564);
            socket.close();
        } catch (Exception e) {
            up = false;
            return;
        }
        up = true;
        ping = System.currentTimeMillis() - before;
    }

    public String getRelayLocation() {
        return relayLocation;
    }
}
