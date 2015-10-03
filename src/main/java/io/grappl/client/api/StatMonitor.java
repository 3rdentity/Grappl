package io.grappl.client.api;

public class StatMonitor {

    private int amountSent;
    private int amountReceived;
    private int connectionsOpened;
    private int grapplsStarted;

    public void dataSent(int size) {
        amountSent += size;
    }

    public void dataReceived(int size) {
        amountReceived += size;
    }

    public void openConnection() {
        connectionsOpened++;
    }

    public void startGrappl() {
        grapplsStarted++;
    }

    public int getSentDataKB() {
        return amountSent / 1000;
    }

    public int getReceivedKB() {
        return amountReceived / 1000;
    }
    public int getSentData() {
        return amountSent;
    }

    public int getReceivedData() {
        return amountReceived;
    }

    public int getOpenConnections() {
        return connectionsOpened;
    }

    public void reset() {
        amountSent = 0;
        amountReceived = 0;
        connectionsOpened = 0;
        grapplsStarted = 0;
    }
}
