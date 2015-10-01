package io.grappl.client.api;

public class StatsManager {

    private int amountSent;
    private int amountReceived;
    private int connectionsOpened;
    private int grapplsStarted;

    public void dataSent(int size) {
        amountSent += size;
        System.out.println(amountReceived + " bytes sent");
    }

    public void dataReceived(int size) {
        amountReceived += size;
        System.out.println(amountReceived + " bytes received");
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
