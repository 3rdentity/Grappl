package io.grappl.client.impl.stable;

import io.grappl.client.api.LocationProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomNetworkProvider implements LocationProvider {

    private List<NetworkLocation> locations = new ArrayList<NetworkLocation>();

    public Random random = new Random();

    public void addLocation(NetworkLocation networkLocation) {
        locations.add(networkLocation);
    }

    public void removeLocation(NetworkLocation networkLocation) {
        locations.remove(networkLocation);
    }

    @Override
    public NetworkLocation getLocation() {
        return locations.get(random.nextInt(locations.size()));
    }
}
