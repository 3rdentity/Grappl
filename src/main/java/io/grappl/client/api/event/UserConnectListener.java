package io.grappl.client.api.event;

import io.grappl.client.impl.event.UserConnectEvent;

public interface UserConnectListener extends Listener {

    public void userConnected(UserConnectEvent userConnectEvent);
}
