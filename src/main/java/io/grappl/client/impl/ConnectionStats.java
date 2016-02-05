package io.grappl.client.impl;

public class ConnectionStats {

    private long timeCreated;

    public ConnectionStats() {
        timeCreated = System.currentTimeMillis();
    }

    public long timeOpen() {
        return System.currentTimeMillis() - timeCreated;
    }
}
