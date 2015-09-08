package io.grappl.client.api.event;

public class UserConnectEvent {
    private String address;

    public UserConnectEvent(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
