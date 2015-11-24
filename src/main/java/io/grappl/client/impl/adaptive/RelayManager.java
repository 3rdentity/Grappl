package io.grappl.client.impl.adaptive;

import java.util.*;

public class RelayManager {

    private Map<String, RelayServer> relayServerMap = new HashMap<String, RelayServer>();

    private Collection<LatencyRecord> records = new ArrayList<LatencyRecord>();

    public void offerRelay(String relay) {
        RelayServer relayServer = new RelayServer(relay);
        relayServer.ping();

        LatencyRecord pingRecord = new LatencyRecord(relayServer, relayServer.getLatency());
        records.add(pingRecord);

        relayServerMap.put(relay, relayServer);
    }

    public RelayServer getRelayForAddress(String location) {
        return relayServerMap.get(location);
    }

    public void pingAll() {
        for(LatencyRecord pingRecord : records) {
            pingRecord.getServer().ping();
        }
    }

    public PriorityQueue<LatencyRecord> getQueue() {
        PriorityQueue<LatencyRecord> records1 = new PriorityQueue<LatencyRecord>(new Comparator<LatencyRecord>() {
            @Override
            public int compare(LatencyRecord o1, LatencyRecord o2) {
                return (o1.getLatency() < o2.getLatency()) ? -1 : 1;
            }
        });
        records1.addAll(records);
        return records1;
    }

    public static void main(String[] args) {
        RelayManager relayManager = new RelayManager();
        relayManager.offerRelay("n.grappl.io");
        relayManager.offerRelay("e.grappl.io");
        relayManager.offerRelay("s.grappl.io");
        relayManager.offerRelay("p.grappl.io");

        PriorityQueue<LatencyRecord> stuff = relayManager.getQueue();
        while(!stuff.isEmpty()) {
            LatencyRecord pingRecord = stuff.poll();
            System.out.println(pingRecord.getServer().getRelayLocation() + " " + pingRecord.getLatency());
        }

        relayManager.pingAll();
        PriorityQueue<LatencyRecord> s2 = relayManager.getQueue();
        while(!s2.isEmpty()) {
            LatencyRecord pingRecord = s2.poll();
            System.out.println(pingRecord.getServer().getRelayLocation() + " " + pingRecord.getLatency());
        }
    }
}
