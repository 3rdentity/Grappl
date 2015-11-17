package io.grappl.client.impl.stable.commands.impl.dummy;

import io.grappl.client.impl.Application;
import io.grappl.client.api.commands.Command;
import io.grappl.client.impl.ApplicationState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Launches a dummy server for testing.
 * Returns large text "'Hello world' -Grappl" in HTML.
 */
public class DummyServerCommand implements Command {

    private ContentSource source;

    public DummyServerCommand() {
        source = new ContentSource() {
            @Override
            public String getContent() {
                return "<h1>'Hello world' -Grappl</h1>";
            }
        };
    }

    public DummyServerCommand(ContentSource contentSource) {
        this.source = contentSource;
    }

    @Override
    public void runCommand(final ApplicationState state, String[] args) {

        try {
            final int port = Integer.parseInt(args[1]);
            state.setCommandBufferVar(args[1]);

            Thread dummyServerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final ServerSocket serverSocket = new ServerSocket(port);

                        Thread dummyServerWebThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while(true) {
                                    try {
                                        Socket socket = serverSocket.accept();

                                        socket.getOutputStream().write(source.getContent().getBytes());
                                        socket.getOutputStream().flush();
                                        socket.getOutputStream().close();
                                        socket.close();
                                    } catch (Exception ignore) {}
                                }
                            }
                        });
                        dummyServerWebThread.setName("Grappl dummy server web thread; port: " + port);
                        dummyServerWebThread.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            dummyServerThread.start();

            Application.getLog().log("Dummy server started at port " + port);
        } catch (ArrayIndexOutOfBoundsException e) {
            Application.getLog().log("You need to provide a port number!");
        }
    }
}
