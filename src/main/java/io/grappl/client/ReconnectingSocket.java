package io.grappl.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ReconnectingSocket {

    private int reconnectDelay;

    private long lastReconnectAttempt;
    private boolean isUp;

    private String location;
    private int port;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private PrintStream printStream;

    private Socket theSocket;

    public ReconnectingSocket(String location, int port, final int reconnectDelay) throws IOException {
        this.reconnectDelay = reconnectDelay;
        this.location = location;
        this.port = port;

        theSocket = new Socket(location, port);
        createStreamObjects();
        isUp = true;

        Thread reconThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    test();

                    try {
                        Thread.sleep(reconnectDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        reconThread.start();
    }

    private void test() {

        if(System.currentTimeMillis() < lastReconnectAttempt + reconnectDelay) return;

        if(!isUp) {
            try {
                theSocket = new Socket(location, port);
                createStreamObjects();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isUp = true;
        }
    }

    private void createStreamObjects() {
        try {
            dataInputStream = new DataInputStream(theSocket.getInputStream());
            dataOutputStream = new DataOutputStream(theSocket.getOutputStream());
            printStream = new PrintStream(dataOutputStream);
        } catch (Exception ignore) {}
    }

    public int getReconnectDelay() {
        return reconnectDelay;
    }

    public Socket getTheSocket() {
        return theSocket;
    }

    public PrintStream getPrintStream() {
        return printStream;
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }
}
