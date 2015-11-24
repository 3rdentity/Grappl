package io.grappl.client.impl.relay.adaptive;

import java.util.*;

public class RelayManager {

    private Map<String, RelayServer> relayServerMap = new HashMap<String, RelayServer>();

    private Collection<LatencyRecord> records = new ArrayList<LatencyRecord>();

    public void offerRelay(RelayServer relayServer) {
        relayServer.ping();

        LatencyRecord pingRecord = new LatencyRecord(relayServer, relayServer.getLatency());
        records.add(pingRecord);

        relayServerMap.put(relayServer.getRelayLocation(), relayServer);
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

    public String[] createList() {
        String[] str = new String[getQueue().size()];

        PriorityQueue<LatencyRecord> latencyRecords = getQueue();

        int i = 0;
        while(!latencyRecords.isEmpty()) {
            LatencyRecord latencyRecord = latencyRecords.poll();

            str[i++] = latencyRecord.getServer().getRelayLocation() + " (" + latencyRecord.getServer().getDescription() + ") " + latencyRecord.getLatency() + "ms";
        }

        return str;
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
