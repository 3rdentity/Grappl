package io.grappl.client.impl.test.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * The default tunnel handler for a Grappl TCP tunnel.
 *
 * Just takes the data buffer that's coming in, and writes it to all the outputs streams
 * in a given direction.
 */
public class GenericHandler implements DataHandler {

    @Override
    public long handleIncoming(byte[] data, long messageSize, List<OutputStream> outwardStreams) {
        for (OutputStream outputStream : outwardStreams) {
            try {
                outputStream.write(data, 0, (int) messageSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return messageSize;
    }

    @Override
    public long handleOutgoing(byte[] data, long messageSize, List<OutputStream> inwardStreams) {
        for (OutputStream outputStream : inwardStreams) {
            try {
                outputStream.write(data, 0, (int) messageSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return messageSize;
    }
}
