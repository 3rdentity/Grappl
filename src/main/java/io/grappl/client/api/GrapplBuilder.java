package io.grappl.client.api;

import io.grappl.GrapplGlobals;
import io.grappl.client.gui.StandardGUI;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class GrapplBuilder {

    private Grappl grappl;

    public GrapplBuilder() {
        grappl = new Grappl();
    }

    public GrapplBuilder withGUI(StandardGUI gui) {
        grappl.gui = gui;
        return this;
    }

    public GrapplBuilder useLoginDetails(String username, char[] password) {
        grappl.username = username;
        grappl.password = password;

        return this;
    }

    public GrapplBuilder atLocalAddress(String serverIP) {
        grappl.internalAddress = serverIP;
        return this;
    }

    public GrapplBuilder atLocalPort(int localPort) {
        grappl.internalPort = localPort;
        return this;
    }

    public GrapplBuilder withInternalLocationProvider(LocationProvider locationProvider) {
        grappl.locationProvider = locationProvider;
        return this;
    }

    /**
     * JFrame is optional
     */
    public GrapplBuilder login(JFrame jFrame) {
        try {
            final int timeOut = 2000;

            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(GrapplGlobals.DOMAIN, GrapplGlobals.AUTHENTICATION), timeOut);
            socket.setSoTimeout(timeOut);

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            try {
                dataOutputStream.writeByte(0);
                PrintStream printStream = new PrintStream(dataOutputStream);
                printStream.println(grappl.username);
                printStream.println(grappl.password);
            } catch (SocketTimeoutException e) {
                if(jFrame != null)
                    JOptionPane.showMessageDialog(jFrame, "Broken connection, authentication failed");
            }

            boolean success = dataInputStream.readBoolean();
            boolean alpha = dataInputStream.readBoolean();
            int port = dataInputStream.readInt();

            try {
                grappl.prefix = dataInputStream.readLine();

                grappl.isPremium = alpha;
                grappl.isLoggedIn = success;

                grappl.externalPort = port + "";
            } catch (SocketTimeoutException e) {
                if(jFrame != null)
                    JOptionPane.showMessageDialog(jFrame, "Login failed, incorrect username or password");
            }

            socket.close();
        } catch (IOException e) {
            if(jFrame != null)
                JOptionPane.showMessageDialog(jFrame, "Broken connection, authentication failed");
        }

        return this;
    }

    public Grappl build() {
        return grappl;
    }
}
