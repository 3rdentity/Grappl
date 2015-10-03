package io.grappl.client.api;

import io.grappl.GrapplGlobals;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Authentication {

    private String username;
    private boolean isPremium;
    private boolean loginSuccessful;
    private String prefix;
    private int staticPort;

    private JFrame optionalJframe;

    public Authentication() {}
    public Authentication(JFrame jFrame) { this.optionalJframe = jFrame; }

    public void login(String username, char[] password) {
        this.username = username;

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
                printStream.println(username);
                printStream.println(password);
            } catch (SocketTimeoutException e) {
                if(optionalJframe != null)
                    JOptionPane.showMessageDialog(optionalJframe, "Broken connection, authentication failed");
            }

            boolean success = dataInputStream.readBoolean();
            boolean alpha = dataInputStream.readBoolean();
            int port = dataInputStream.readInt();

            try {
                prefix = dataInputStream.readLine();

                isPremium = alpha;
                loginSuccessful = success;

                staticPort = port;
            } catch (SocketTimeoutException e) {
                if(optionalJframe != null)
                    JOptionPane.showMessageDialog(optionalJframe, "Login failed, incorrect username or password");
            }

            socket.close();
        } catch (IOException e) {
            if(optionalJframe != null)
                JOptionPane.showMessageDialog(optionalJframe, "Broken connection, authentication failed");
        }
    }

    public String getUsername() {
        return username;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public boolean wasLoginSuccessful() {
        return loginSuccessful;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getStaticPort() {
        return staticPort;
    }
}
