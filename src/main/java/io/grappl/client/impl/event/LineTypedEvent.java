package io.grappl.client.impl.event;

public class LineTypedEvent {

    private String line;

    public LineTypedEvent(String line) {
        this.line = line;
    }

    public String getLine() {
        return line;
    }
}
