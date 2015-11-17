package io.grappl.client.impl.test.handler;

import java.io.OutputStream;
import java.util.List;

public interface DataHandler {

    public long handleIncoming(byte[] data, long messageSize, List<OutputStream> outwardStream);

    public long handleOutgoing(byte[] data, long messageSize, List<OutputStream> inwardStreams);
}
