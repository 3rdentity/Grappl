package io.grappl.client.impl.event;

import io.grappl.client.impl.relay.RelayServer;

public class RelayPingEvent {

    private RelayServer relayServer;
    private int ping;

    public RelayPingEvent(RelayServer relayServer, int ping) {
        this.relayServer = relayServer;
        this.ping = ping;
    }

    public RelayServer getRelayServer() {
        return relayServer;
    }

    public int getPing() {
        return ping;
    }
}
