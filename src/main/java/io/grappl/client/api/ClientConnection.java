package io.grappl.client.api;

public interface ClientConnection {

    public void open();
    public void close();

    public Grappl getGrappl();
}
