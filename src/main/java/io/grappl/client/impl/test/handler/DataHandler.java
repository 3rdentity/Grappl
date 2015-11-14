package io.grappl.client.impl.test.handler;

public interface DataHandler {

    public void handleIncoming(byte[] data, long messageSize);

    public void handleOutgoing(byte[] data, long messageSize);
}
