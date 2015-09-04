package io.grappl.client.experiments.freezer;

public class TemporalBlock {

    public long timeActive;
    public byte[] data;
    private Freezer freezer;

    public TemporalBlock(Freezer freezer, byte[] data) {
        this.freezer = freezer;
        timeActive = System.currentTimeMillis() - getFreezer().freezerID;
        this.data = data;
    }

    public Freezer getFreezer() {
        return freezer;
    }
}
