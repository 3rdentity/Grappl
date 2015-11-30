package io.grappl.client.api;

import io.grappl.client.impl.NetworkLocation;

public interface LocationProvider {

    public NetworkLocation getLocation();
}
