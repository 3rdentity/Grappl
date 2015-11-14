package io.grappl.client.impl.test.handler.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpState {

    private List<IncomingHttpMessage> incomingHttpMessageList;

    private Map<String, OutgoingHttpMessage> cachedPage = new HashMap<String, OutgoingHttpMessage>();

    public void acknowledgeIncoming(IncomingHttpMessage incomingHttpMessage) {

        String raw = incomingHttpMessage.getRaw();
    }

    public void acknowledgeOutgoing(OutgoingHttpMessage outgoingHttpMessage) {

    }
}