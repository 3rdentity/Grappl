package io.grappl.client.api.handler.http;

import com.google.gson.Gson;

public class OutgoingHttpMessage {

    private String raw;

    public OutgoingHttpMessage(String raw) {
        this.raw = raw;
    }

    public String getRaw() {
        return raw;
    }
}
