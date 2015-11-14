package io.grappl.client.impl.stable;

public class NetworkLocation {

    private String address;
    private int port;

    public NetworkLocation(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return address + ":" + port;
    }
}
