package io.grappl.client.api;

import io.grappl.GrapplGlobal;
import io.grappl.client.GrapplGUI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class GrapplBuilder {

    private Grappl grappl;

    public GrapplBuilder() {
        grappl = new Grappl();
    }

    public GrapplBuilder withGUI(GrapplGUI gui) {
        grappl.gui = gui;
        return this;
    }

    public GrapplBuilder useLoginDetails(String username, char[] password) {
        grappl.username = username;
        grappl.password = password;

        return this;
    }

    public GrapplBuilder atLocalPort(int localPort) {
        grappl.internalPort = localPort;
        return this;
    }

    public GrapplBuilder login() {
        try {
            Socket socket = new Socket(GrapplGlobal.DOMAIN, GrapplGlobal.AUTHENTICATION);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            dataOutputStream.writeByte(0);

            try {
                PrintStream printStream = new PrintStream(dataOutputStream);
                printStream.println(grappl.username);
                printStream.println(grappl.password);
            } catch (Exception e) {
                e.printStackTrace();
            }


            boolean success = dataInputStream.readBoolean();
            boolean alpha = dataInputStream.readBoolean();
            int port = dataInputStream.readInt();

            System.out.println(success);
            grappl.prefix = dataInputStream.readLine();

            grappl.isAlphaTester = alpha;
            grappl.isLoggedIn = success;

            grappl.externalPort = port + "";
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    public Grappl build() {
        return grappl;
    }
}
