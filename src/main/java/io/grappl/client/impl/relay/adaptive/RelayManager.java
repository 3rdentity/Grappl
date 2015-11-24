package io.grappl.client.impl.relay.adaptive;

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

        PriorityQueue<RelayServer> latencyRecords = getQueue();

        int i = 0;
        while(!latencyRecords.isEmpty()) {
            RelayServer latencyRecord = latencyRecords.poll();

            str[i++] = latencyRecord.getRelayLocation() + " (" + latencyRecord.getDescription() + ") " + latencyRecord.getLatency() + "ms";
        }

        return str;
    }

    public RelayTransmission getRelayTransmission() {
        RelayTransmission relayTransmission = new RelayTransmission();

        System.out.println(records.size());
        for(RelayServer latencyRecord : records) {
            relayTransmission.getRelayServerList().add(latencyRecord);
        }

        System.out.println(relayTransmission.getRelayServerList().size());

        return relayTransmission;
    }

    public static void main(String[] args) {
//        RelayManager relayManager = new RelayManager();
//        relayManager.offerRelay("n.grappl.io","");
//        relayManager.offerRelay("e.grappl.io","");
//        relayManager.offerRelay("s.grappl.io");
//        relayManager.offerRelay("p.grappl.io");
//
//        PriorityQueue<LatencyRecord> stuff = relayManager.getQueue();
//        while(!stuff.isEmpty()) {
//            LatencyRecord pingRecord = stuff.poll();
//            System.out.println(pingRecord.getServer().getRelayLocation() + " " + pingRecord.getLatency());
//        }
//
//        relayManager.pingAll();
//        PriorityQueue<LatencyRecord> s2 = relayManager.getQueue();
//        while(!s2.isEmpty()) {
//            LatencyRecord pingRecord = s2.poll();
//            System.out.println(pingRecord.getServer().getRelayLocation() + " " + pingRecord.getLatency());
//        }
    }
}
