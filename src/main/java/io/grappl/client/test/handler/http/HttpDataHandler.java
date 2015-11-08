package io.grappl.client.test.handler.http;

import io.grappl.client.test.handler.DataHandler;

public class HttpDataHandler implements DataHandler {

    private boolean beVerbose = false;
    private HttpState httpState;

    public HttpDataHandler() {
        httpState = new HttpState();
    }

    @Override
    public void handleIncoming(byte[] data, long messageSize) {

        IncomingHttpMessage incomingHttpMessage = new IncomingHttpMessage(new String(data));
        httpState.acknowledgeIncoming(incomingHttpMessage);

        if(beVerbose) {
            System.out.println("Incoming message:");
            System.out.println(incomingHttpMessage.getRaw());
            System.out.println("I-Message size: " + messageSize + " bytes");
        }
    }

    @Override
    public void handleOutgoing(byte[] data, long messageSize) {

        OutgoingHttpMessage outgoingHttpMessage = new OutgoingHttpMessage(new String(data));
        httpState.acknowledgeOutgoing(outgoingHttpMessage);

        if(beVerbose) {
            System.out.println("Outgoing message:");
            System.out.println(new String(data));
            System.out.println("O-Message size: " + messageSize + " bytes");
        }
    }
}
