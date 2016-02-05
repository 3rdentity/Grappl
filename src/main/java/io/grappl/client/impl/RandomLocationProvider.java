package io.grappl.client.impl;

import io.grappl.client.api.LocationProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Instead of being a LocationProvider that always returns one location, or
 * fetches a location deterministically, this one just picks from a pool randomly
 * every time you use it.
 *
 * You never know where you're going to end up!
 */
public class RandomLocationProvider implements LocationProvider {

    private List<NetworkLocation> locations = new ArrayList<NetworkLocation>();

    private Random random = new Random();

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
