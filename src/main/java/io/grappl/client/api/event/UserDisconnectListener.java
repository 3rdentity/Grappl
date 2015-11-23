package io.grappl.client.api.event;

import io.grappl.client.impl.stable.event.UserDisconnectEvent;

public interface UserDisconnectListener {

    public void userDisconnected(UserDisconnectEvent userDisconnectEvent);
}
