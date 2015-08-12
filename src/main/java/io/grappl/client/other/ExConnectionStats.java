package io.grappl.client.other;

public class ExConnectionStats {

    private long timeCreated;

    public ExConnectionStats() {
        timeCreated = System.currentTimeMillis();
    }

    public long timeOpen() {
        return System.currentTimeMillis() - timeCreated;
    }
}
