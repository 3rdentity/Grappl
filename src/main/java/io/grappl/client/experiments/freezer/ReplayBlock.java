package io.grappl.client.experiments.freezer;

public class ReplayBlock {

    public long time;
    public byte[] data;

    public ReplayBlock(long time, byte[] data) {
        this.time = time;
        this.data = data;
    }
}
