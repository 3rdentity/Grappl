package io.grappl.client.impl.relay;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.error.RelayServerNotFoundException;

import java.util.PriorityQueue;

public class AdaptiveConnector {

    private RelayManager relayManager;

    public AdaptiveConnector(RelayManager relayManager) {
        this.relayManager = relayManager;
    }

    public void subject(Grappl grappl) {
        PriorityQueue<RelayServer> relayServers = relayManager.getQueue();

        while(!relayServers.isEmpty()) {
            RelayServer relayServer = relayServers.poll();

            relayServer.ping();

            if(relayServer.isUp()) {
                try {
                    grappl.connect(relayServer.getRelayLocation());
                } catch (RelayServerNotFoundException e) {
                    e.printStackTrace();
                }

                return;
            }
        }
    }
}
