package io.grappl.client.impl.stable;

public class ExConnectionStats {

    private long timeCreated;

    public ExConnectionStats() {
        timeCreated = System.currentTimeMillis();
    }

    public long timeOpen() {
        return System.currentTimeMillis() - timeCreated;
    }
}
