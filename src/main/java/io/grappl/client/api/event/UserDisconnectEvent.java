package io.grappl.client.api.event;

import java.net.InetAddress;

public class UserDisconnectEvent {
    private String address;

    public UserDisconnectEvent(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
