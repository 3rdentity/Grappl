package io.grappl.client.api.event;

public interface ConsoleMessageListener extends Listener {

    public void receiveMessage(String message);
}
