package io.grappl.client.impl.relay.adaptive;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.error.RelayServerNotFoundException;

import java.util.PriorityQueue;

public class AdaptiveConnector {

    private RelayManager relayManager;

    public AdaptiveConnector(RelayManager relayManager) {
        this.relayManager = relayManager;
    }

    public void subject(Grappl grappl) {
        PriorityQueue<LatencyRecord> latencyRecords = relayManager.getQueue();

        while(!latencyRecords.isEmpty()) {
            LatencyRecord latencyRecord = latencyRecords.poll();

            latencyRecord.getServer().ping();

            if(latencyRecord.getServer().isUp()) {
                try {
                    grappl.connect(latencyRecord.getServer().getRelayLocation());
                } catch (RelayServerNotFoundException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    public RelayManager getRelayManager() {
        return relayManager;
    }
}
