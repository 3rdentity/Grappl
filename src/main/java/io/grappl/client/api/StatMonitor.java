package io.grappl.client.api;

public class StatMonitor {

    private int amountSent;
    private int amountReceived;
    private int connectionsOpen;
    private int totalConnections;
    private int grapplsStarted;

    public void dataSent(int size) {
        amountSent += size;
    }

    public void dataReceived(int size) {
        amountReceived += size;
    }

    public void openConnection() {
        connectionsOpen++;
    }

    public void closeConnection() { connectionsOpen--; }

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
        return connectionsOpen;
    }

    public void reset() {
        amountSent = 0;
        amountReceived = 0;
        connectionsOpen = 0;
        grapplsStarted = 0;
    }
}
