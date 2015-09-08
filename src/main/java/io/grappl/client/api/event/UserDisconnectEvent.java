package io.grappl.client.api.event;

public class UserDisconnectEvent {
    private String address;

    public UserDisconnectEvent(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
