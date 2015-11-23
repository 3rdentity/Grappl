package io.grappl.client.api;

import io.grappl.client.impl.stable.NetworkLocation;

public interface LocationProvider {

    public NetworkLocation getLocation();
}
