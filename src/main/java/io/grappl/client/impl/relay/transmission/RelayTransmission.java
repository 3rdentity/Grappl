package io.grappl.client.impl.relay.transmission;

import com.google.gson.Gson;

import io.grappl.client.impl.relay.adaptive.RelayServer;

import java.util.ArrayList;
import java.util.List;

public class RelayTransmission {

    private List<RelayServer> relayServerList = new ArrayList<RelayServer>();

    public void addRelayServer(RelayServer relayServer) {
        relayServerList.add(relayServer);
    }

    public List<RelayServer> getRelayServerList() {
        return relayServerList;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static RelayTransmission getRelayTransmissionFrom(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, RelayTransmission.class);
    }
}
