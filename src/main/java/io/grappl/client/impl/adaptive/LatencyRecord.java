package io.grappl.client.impl.adaptive;

public class LatencyRecord {

    private RelayServer server;

    public LatencyRecord(RelayServer server, long ping) {
        this.server = server;
    }

    public RelayServer getServer() {
        return server;
    }

    public long getLatency() {
        return server.getLatency();
    }
}
