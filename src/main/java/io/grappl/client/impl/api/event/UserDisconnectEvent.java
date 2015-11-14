package io.grappl.client.impl.api.event;

public class UserDisconnectEvent {

    private String address;

    public UserDisconnectEvent(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
