package io.grappl.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;

/**
 * A host represents the connection between the server and an open Grappl client.
 * It is connected to a service-server, which runs on the assigned localPort on the GrapplServer.
 * ExClients can connect to that service-server. When they do, a message is sent to the Grappl client
 * associated with that connection, and it will open a traffic-client to the traffic-server.
 *
 * Each connection (ExClient-GrapplServer-Client) spawns two threads on the GrapplServer;
 * one is for reading data, one is for writing data.
 *
 * An additional thread is maintained to handle the host loop.
 */
public class Host {

    // The address (IP) of the Grappl client associated with this connection.
    private String address;

    // The message socket open to the associated Grappl client.
    private Socket messageSocket;

    // Whether or not the ExServer is open.
    private boolean open = false;

    // The localPort number the ExServer is running on.
    private int portNumber;

    // The number of ExClients currently connected to the ExServer. This variable is not always accurate. (Fix?)
    private int clientCount = 0;

    // The user account associated with this host. Currently unused.
    private User user;

    // The time the host opened
    private long timeOpened;

    // The traffic server
    private ServerSocket trafficSocket;
    private ServerSocket serviceServer;

    /**
     * Construct a host object.
     * @param socket the message-socket associated with this host
     * @param user the user account associated with this host
     */
    public Host(final Socket socket, final User user) {
        this.messageSocket = socket;
        this.address = socket.getInetAddress().toString();
        this.user = user;

        timeOpened = System.currentTimeMillis();

        // Display debug message for VPS-user to see.
        log("Host connected " + socket.getInetAddress());

        PrintStream printStream = null;
        try {
            printStream = new PrintStream(Server.relaySocket.getOutputStream());
            printStream.println("0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the host.
     *
     * Activities this involves:
     * - Getting the ExServer's localPort (either randomized or attached to the User object)
     * - Creating the ExServer.
     * - Initializing the host thread. This waits for ExClient connections.
     * - Passing messages to the GrClient.
     */
    public void start() {
        try {
            final Host thisHost = this;
            portNumber = Server.getPort(getUser(), thisHost.getAddress());

            final int trafficPortNumber = portNumber + 1;
//            System.out.println("opa: " + trafficPortNumber);
            trafficSocket = new ServerSocket(trafficPortNumber);

            // Tell the host what localPort they're running on.
            final PrintStream printStream = new PrintStream(messageSocket.getOutputStream());
            printStream.println(getPortNumber());

            serviceServer = new ServerSocket(getPortNumber());

            if(getUser() != null) {
                if(getUser().getUsername() != null) {
                    log("Hosting connections at port: " + getPortNumber() + " | Server hosted by: " + getUser().getUsername());
                } else {
                    log("Hosting connections at port: " + getPortNumber());
                }
            } else {
                log("Hosting connections at port: " + getPortNumber());
            }

            open = true;

            /**
             * This thread loops over and over attempting to connect ExClients to the Host until something in the loop
             * throws an exception, in which case the thread prints an error message, cleans up, and disconnects
             * the host.
             */
            Thread hostServer = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            // Receive external client connection
                            final Socket local = serviceServer.accept();

                            // Display message that a client is attempting to connect
                            if(Server.detailedDebug) log(getPortNumber() + ": Exclient:(" + local.getInetAddress() + ") is attempting to connect to host:(" +
                                    messageSocket.getInetAddress() + ")");

                            if(!getUser().isIPBanned(local.getInetAddress().toString())) {
                                // Inform host that something is coming
                                printStream.println(local.getInetAddress().toString());

                                // If host is still open
                                if(System.currentTimeMillis() < getTick() + 2000) {
                                    // Attempt to launch connection
                                    launchNewConnection(local);
                                } else {
                                    thisHost.closeHost();
                                }
                            }
                        }
                    } catch (Exception e) {
                        try {
                            serviceServer.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        closeHost();
                    }
                }
            });
            hostServer.start();

        } catch (Exception e) {
            e.printStackTrace();
            open = false;
        }
    }

    /**
     * If the ExServer is currently open, close the host.
     */
    public void closeHost() {
        if(open) {
            open = false;

            log(getPortNumber() + ": Host closed at port: " + getPortNumber() + "(" + address + ")");

            Server.removeHost(this);

            PrintStream printStream;
            try {
                printStream = new PrintStream(Server.relaySocket.getOutputStream());
                printStream.println("1");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                messageSocket.close();
                serviceServer.close();
                trafficSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                serviceServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                trafficSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void log(String log) {
        String tag = DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
        System.out.println("[" + tag + "] " + log);
    }

    /**
     * Launches a new 'connections'.
     *
     * A connection is the bridge between an ExClient and a GrClient.
     * @param local the socket connecting the ExServer to the ExClient
     */
    public void launchNewConnection(final Socket local) {
        // Increment the number of clients connected. May or may not decrement, ever. Probably doesn't.
        clientCount++;

        if(getUser() != null) {
            getUser().connectionsTotal++;
        }

        // Create the actual connection thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get traffic socket.
                    final Socket remote = trafficSocket.accept();

                    if(Server.detailedDebug) log(getPortNumber() + ": Exclient:(" + local.getInetAddress() + ") has connected to host:(" +
                            messageSocket.getInetAddress() + ")");

                    Thread localToRemote = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            byte[] buffer = new byte[4096];
                            int size;

                            try {
                                while ((size = local.getInputStream().read(buffer)) != -1) {
                                    remote.getOutputStream().write(buffer, 0, size);

                                    if(getUser() != null) {
                                        getUser().blocksIn++;
                                    }

                                    try {
                                        Thread.sleep(5);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                try {
                                    local.close();
                                    remote.close();
                                } catch (IOException e1) {
//                                    e1.printStackTrace();
                                }
                            }

                            try {
                                local.close();
                                remote.close();
                            } catch (IOException e) {
//                                e.printStackTrace();
                            }
                        }
                    });
                    localToRemote.start();

                    final Thread remoteToLocal = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            byte[] buffer = new byte[4096];
                            int size;

                            try {
                                while ((size = remote.getInputStream().read(buffer)) != -1) {
                                    local.getOutputStream().write(buffer, 0, size);

                                    if(getUser() != null) {
                                        getUser().blocksOut++;
                                    }

                                    try {
                                        Thread.sleep(5);
                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                try {
                                    local.close();
                                    remote.close();
                                } catch (IOException e1) {
//                                    e1.printStackTrace();
                                }
                            }

                            try {
                                local.close();
                                remote.close();
                            } catch (IOException e) {
//                                e.printStackTrace();
                            }
                        }
                    });
                    remoteToLocal.start();

                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        }).start();
    }

    public long getTimeOpened() {
        return timeOpened;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public boolean isOpen() {
        return open;
    }

    public User getUser() {
        return user;
    }

    public int connectionCount() {
        return clientCount;
    }

    public long getTick() {
        return Server.getHostTick(address);
    }

    public String getAddress() {
        return address;
    }
}
