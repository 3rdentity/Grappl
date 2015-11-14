package io.grappl.client.impl.test.handler.http;

public class OutgoingHttpMessage {

    private String raw;

    public OutgoingHttpMessage(String raw) {
        this.raw = raw;
    }

    public String getRaw() {
        return raw;
    }
}
