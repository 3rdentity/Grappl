package io.grappl.client.impl.test.handler;

import java.io.OutputStream;
import java.util.List;

/**
 * Null object pattern
 */
public class NullHandler implements DataHandler {

    @Override
    public long handleIncoming(byte[] data, long messageSize, List<OutputStream> outwardStream) {
        return 0;
    }

    @Override
    public long handleOutgoing(byte[] data, long messageSize, List<OutputStream> inwardStreams) {
        return 0;
    }
}
