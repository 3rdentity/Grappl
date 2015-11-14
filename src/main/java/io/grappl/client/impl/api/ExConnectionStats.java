package io.grappl.client.impl.api;

public class ExConnectionStats {

    private long timeCreated;

    public ExConnectionStats() {
        timeCreated = System.currentTimeMillis();
    }

    public long timeOpen() {
        return System.currentTimeMillis() - timeCreated;
    }
}
