package io.grappl.client.api.commands.impl;

import io.grappl.client.Application;
import io.grappl.client.api.Grappl;
import io.grappl.client.api.commands.Command;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Launches a dummy server for testing.
 * Returns large text "'Hello world' -Grappl" in HTML.
 */
public class DummyServer implements Command {

    @Override
    public void runCommand(Grappl grappl, String[] args) {

        try {
            final int port = Integer.parseInt(args[1]);

            Thread dummyServerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ServerSocket serverSocket = new ServerSocket(port);

                        Socket socket = serverSocket.accept();

                        socket.getOutputStream().write("<h1>'Hello world' -Grappl</h1>".getBytes());
                        socket.getOutputStream().flush();
                        socket.getOutputStream().close();
                        socket.close();

                        while (true) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
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
