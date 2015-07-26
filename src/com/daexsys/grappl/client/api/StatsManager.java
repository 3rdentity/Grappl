package com.daexsys.grappl.client.api;

public class StatsManager {

    private int blocksSent;
    private int blocksReceived;
    private int connectionsOpened;
    private int grapplsStarted;

    public void sendBlock() {
        blocksSent++;
    }

    public void receiveBlock() {
        blocksReceived++;
    }

    public void openConnection() {
        connectionsOpened++;
    }

    public void startGrappl() {
        grapplsStarted++;
    }

    public int getSentData() {
        return blocksSent;
    }

    public int getReceivedData() {
        return blocksReceived;
    }

    public int getOpenConnections() {
        return connectionsOpened;
    }

    public void reset() {
        blocksSent = 0;
        blocksReceived = 0;
        connectionsOpened = 0;
        grapplsStarted = 0;
    }
}
