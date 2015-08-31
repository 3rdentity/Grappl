package io.grappl.client.api.event;

import java.net.InetAddress;

public class UserConnectEvent {
    private String address;

    public UserConnectEvent(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
