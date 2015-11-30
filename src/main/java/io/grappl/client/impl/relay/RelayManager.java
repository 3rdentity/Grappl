package io.grappl.client.impl.relay;

import io.grappl.client.impl.relay.RelayServer;
import io.grappl.client.impl.relay.RelayTransmission;

import java.util.*;

public class RelayManager {

    private Map<String, RelayServer> relayServerMap = new HashMap<String, RelayServer>();

    private Collection<RelayServer> records = new ArrayList<RelayServer>();

    public void offerRelay(RelayServer relayServer) {
        relayServer.ping();

        records.add(relayServer);

        relayServerMap.put(relayServer.getRelayLocation(), relayServer);
    }

    public RelayServer getRelayForAddress(String location) {
        return relayServerMap.get(location);
    }

    public void pingAll() {
        for(RelayServer server : records) {
            server.ping();
        }
    }

    public PriorityQueue<RelayServer> getQueue() {
        PriorityQueue<RelayServer> records1 = new PriorityQueue<RelayServer>(new Comparator<RelayServer>() {
            @Override
            public int compare(RelayServer o1, RelayServer o2) {
                return (o1.getLatency() < o2.getLatency()) ? -1 : 1;
            }
        });
        records1.addAll(records);
        return records1;
    }

    public String[] createList() {
        String[] str = new String[getQueue().size()];

        PriorityQueue<RelayServer> relayServers = getQueue();

        int i = 0;
        while(!relayServers.isEmpty()) {
            RelayServer relayServer = relayServers.poll();

            str[i++] = relayServer.getRelayLocation()
                    + " (" + relayServer.getDescription() + ") "
                    + relayServer.getLatencyMessage();
        }

        return str;
    }

    public RelayTransmission getRelayTransmission() {
        RelayTransmission relayTransmission = new RelayTransmission();

        for(RelayServer relayServer : records) {
            relayTransmission.getRelayServerList().add(relayServer);
        }

        return relayTransmission;
    }
}
