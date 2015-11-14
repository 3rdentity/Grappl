package io.grappl.client.impl.test.handler.http;

public class IncomingHttpMessage {

    private String raw;

    public IncomingHttpMessage(String raw) {
        this.raw = raw;
    }

    public String getRaw() {
        return raw;
    }
}
