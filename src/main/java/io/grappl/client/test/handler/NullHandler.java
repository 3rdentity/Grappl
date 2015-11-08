package io.grappl.client.test.handler;

/**
 * Null object pattern
 */
public class NullHandler implements DataHandler {

    @Override
    public void handleIncoming(byte[] data, long messageSize) {

    }

    @Override
    public void handleOutgoing(byte[] data, long messageSize) {

    }
}
