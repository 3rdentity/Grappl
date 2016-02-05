package io.grappl.client.api.event;

import io.grappl.client.impl.event.UserDisconnectEvent;

public interface UserDisconnectListener extends Listener {

    public void userDisconnected(UserDisconnectEvent userDisconnectEvent);
}
