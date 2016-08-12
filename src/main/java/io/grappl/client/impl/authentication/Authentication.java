package io.grappl.client.impl.authentication;

import io.grappl.client.impl.Application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;

/**
 * This class represents the state of an authentication session with the core Grappl auth
 * server. It contains data such as username, reserved port, and whether or not the user
 * is a premium user (donator).
 *
 * @see AuthenticationException
 */
public class Authentication {

    /* Data related to the logged in user. */
    private String username;
    private boolean isPremium;
    private int reservedPort;

    /* Whether or not this session is a state of being logged in. */
    private boolean loggedIn = false;

    /* Networks objects for this session, used to communicate with the core server. */
    private Socket authSocket;
    private DataInputStream authDataInputStream;
    private DataOutputStream authDataOutputStream;

    /* Unique ID for this auth session. Primarily used to discriminate between sessions in logs. */
    private UUID connectionUUID = UUID.randomUUID();

    protected Authentication() {
        Application.getLog().log("Auth connection created " + getConnectionUUID());
    }

    protected void createSession(final String username, final char[] password) throws AuthenticationException {
        this.username = username;

        try {
            final int timeOut = 2000; // Set connection time-out time to 2 seconds.

            authSocket = new Socket();
            authSocket.connect(new InetSocketAddress(Authenticator.CORE_DOMAIN, Authenticator.AUTHENTICATION_PORT), timeOut);
            authSocket.setSoTimeout(timeOut);

            this.authDataInputStream = new DataInputStream(authSocket.getInputStream());
            this.authDataOutputStream = new DataOutputStream(authSocket.getOutputStream());

            try {
                authDataOutputStream.writeByte(0);
                PrintStream printStream = new PrintStream(authDataOutputStream);
                printStream.println(username);
                printStream.println(password);
            } catch (SocketTimeoutException e) { throw new AuthenticationException("Connection timed out."); }

            boolean loginSuccessful = authDataInputStream.readBoolean();
            boolean isUserPremium = authDataInputStream.readBoolean();
            int reservedPort = authDataInputStream.readInt();

            try {
                // TODO: Remove this line eventually- it is un-needed functionality. Unfortunately removing it now would break things.
                authDataInputStream.readLine();

                this.isPremium = isUserPremium;
                this.loggedIn = loginSuccessful;
                this.reservedPort = reservedPort;
            } catch (SocketTimeoutException e) {
                throw new AuthenticationException("Wrong credentials for: " + username + " (or account does not exist)");
            }
            authSocket.setSoTimeout(100000000);
        } catch (IOException e) {
            throw new AuthenticationException("Login failed");
        }
    }

    public void closeSession() {
        loggedIn = false;
        isPremium = false;
        reservedPort = -1;

        Application.getLog().log("Authentication session closed " + getConnectionUUID().toString());

        try {
            authSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        authSocket = null;
    }

    public String getUsername() {
        return username;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public int getReservedPort() {
        return reservedPort;
    }

    public UUID getConnectionUUID() {
        return connectionUUID;
    }

    public void banIP(String ip) throws IOException {
        authDataOutputStream.writeByte(AuthenticationPackets.BAN_IP);
        PrintStream printStream = new PrintStream(getAuthDataOutputStream());
        printStream.println(ip);
    }

    public void unbanIP(String ip) throws IOException {
        authDataOutputStream.writeByte(AuthenticationPackets.UNBAN_IP);
        PrintStream printStream = new PrintStream(getAuthDataOutputStream());
        printStream.println(ip);
    }

    public boolean changeReservedPortTo(final int port) throws IOException {
        authDataOutputStream.writeByte(AuthenticationPackets.CHANGE_RESERVED_PORT);
        authDataOutputStream.writeInt(port);
        return getAuthDataInputStream().readBoolean();
    }

    private DataInputStream getAuthDataInputStream() {
        return authDataInputStream;
    }

    private DataOutputStream getAuthDataOutputStream() {
        return authDataOutputStream;
    }

    @Override
    public String toString() {
        return "Authentication{" +
                "username='" + username + '\'' +
                '}';
    }
}
