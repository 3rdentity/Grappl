package io.grappl.server;

import io.grappl.GrapplGlobal;
import io.grappl.server.restartrestore.Union;
import io.grappl.web.GrapplStats;
import io.grappl.web.WebServer;
import io.grappl.web.cgi.PortHandler;
import io.grappl.web.list.ServerList;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.util.*;

public class Server {

    public static ServerSocket trafficServer;
    public static ServerSocket messageServer;
    public static ServerSocket heartBeatServer;

    public static Socket relaySocket;

    public static ServerSocket relayControl;

    public static boolean isHead = false;

    public static List<Host> hosts = new ArrayList<Host>();

    public static Map<String, Long> heartBeats = new HashMap<String, Long>();
    public static Map<String, Host> hostMap = new HashMap<String, Host>();
    public static Map<Integer, Host> portMap = new HashMap<Integer, Host>();
    public static Map<String, String> ipToUser = new HashMap<String, String>();

    public static Map<String, Integer> ipToPort = new HashMap<String, Integer>();
    public static Map<String, Socket> relayServers = new HashMap<String, Socket>();

    public static Set<Integer> portsTaken = new HashSet<Integer>();

    public static boolean detailedDebug = false;

    public static Map<String, Boolean> relayMap = new HashMap<String, Boolean>();

    public static boolean connectionLost = false;

    private static Map<String, Integer> connectionsPerIP = new HashMap<String, Integer>();

    public static void main(String[] args) {
        GrapplServerState.setup();

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("relay")) {
            } else {
                isHead = true;
                // Start the web server
                WebServer.main(null);
            }
        } else {
            isHead = true;
            // Start the web server
            WebServer.main(null);
        }

        log("GrapplServer started.");
        log("Waiting for connections.");
        log("Head status: " + isHead);

        if(!isHead) {
            openRelay();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ServerSocket serverSocket = new ServerSocket(GrapplGlobal.AUTHENTICATION);

                        while (true) {
                            final Socket conn = serverSocket.accept();
                            final String address = serverSocket.getInetAddress().toString();

                            try {

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String ip = "";
                                        try {

                                            DataInputStream dataInputStream = new DataInputStream(conn.getInputStream());
                                            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                                            while(true) {
                                                try {
                                                    byte packetNum = dataInputStream.readByte();

                                                    // Login
                                                    if(packetNum == 0) {
                                                        String username = dataInputStream.readLine();
                                                        String pass = dataInputStream.readLine();

                                                        if(UserManager.getUserByName((username)).getPassword().equalsIgnoreCase(pass + "")) {
                                                            dataOutputStream.writeBoolean(true);
                                                            dataOutputStream.writeBoolean(UserManager.getUserByName(username).isAlphaTester());
                                                            dataOutputStream.writeInt(UserManager.getUserByName(username).getPort());

                                                            PrintStream printStream = new PrintStream(dataOutputStream);
                                                            String subdomain = serverForIP(conn.getInetAddress());
                                                            printStream.println(subdomain);

                                                            int port = Server.getPort(UserManager.getUserByName(username), conn.getInetAddress().toString());

                                                            if(!UserManager.getUserByName(username).isAlphaTester()) {
                                                                port = -1;
                                                            }

                                                            // If the server is the australia one

                                                        if(subdomain.equalsIgnoreCase("n")) {
                                                            DataOutputStream dataOutputStream1 = new DataOutputStream(relayServers.get("NYC").getOutputStream());
                                                            PrintStream printStream1 = new PrintStream(dataOutputStream1);
                                                            printStream1.println("INCOMING");
                                                            String tOSend = conn.getInetAddress() + " " + port;
                                                            System.out.println(tOSend);
                                                            printStream1.println(tOSend);
                                                            printStream1.println(username);
                                                        }

                                                        if(subdomain.equalsIgnoreCase("e")) {
                                                            DataOutputStream dataOutputStream1 = new DataOutputStream(relayServers.get("AMS").getOutputStream());
                                                            PrintStream printStream1 = new PrintStream(dataOutputStream1);
                                                            printStream1.println("INCOMING");
                                                            String tOSend = conn.getInetAddress() + " " + port;
                                                            System.out.println(tOSend);
                                                            printStream1.println(tOSend);
                                                            printStream1.println(username);
                                                        }

                                                        if(subdomain.equalsIgnoreCase("p")) {
                                                                DataOutputStream dataOutputStream1 = new DataOutputStream(relayServers.get("AUSTRALIA").getOutputStream());
                                                                PrintStream printStream1 = new PrintStream(dataOutputStream1);
                                                            printStream1.println("INCOMING");
                                                                printStream1.println(conn.getInetAddress() + " " + port);
                                                            printStream1.println(username);
                                                            }

                                                            ipToUser.put(conn.getInetAddress().toString(), username);
                                                        } else {
                                                            dataOutputStream.writeBoolean(false);
                                                            dataOutputStream.writeBoolean(false);
                                                            dataOutputStream.writeInt(-1);
                                                        }
                                                    }

                                                    else if(packetNum == 2) {
                                                        int port = dataInputStream.readInt();

                                                        User user = userForIP(conn.getInetAddress().toString());
                                                        if(PortHandler.portExists(port)) {
                                                            user.setFixedPort(port);
                                                        }
                                                    }

                                                    else if(packetNum == 5) {
                                                        String aip = dataInputStream.readLine();
                                                        User user = userForIP(conn.getInetAddress().toString());
                                                        user.banIP(aip);
                                                    }

                                                    else if(packetNum == 6) {
                                                        ip = dataInputStream.readLine();
                                                        System.out.println("Adding host to server list: " + ip);
                                                        ServerList.addHost(ip);
                                                    }

                                                    else if(packetNum == 7) {
                                                        String sdf = dataInputStream.readLine();
                                                        System.out.println("Remove host from server list: " + ip);

                                                        // Should collect data about session

                                                        ServerList.removeHost(ip);
                                                    }

                                                    else if(packetNum == 8) {
                                                        String aip = dataInputStream.readLine();
                                                        User user = userForIP(conn.getInetAddress().toString());
                                                        user.unbanIP(aip);
                                                    }

                                                } catch (Exception e) {
                                                    conn.close();

                                                    ServerList.removeHost(ip);
                                                }

                                                try {
                                                    Thread.sleep(50);
                                                } catch (Exception e) {}
                                            }
                                        } catch(Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                            } catch (Exception e) {

                                String regionCode = getCode(address.substring(1, address.length()));
                                relayMap.put(regionCode, false);
                                e.printStackTrace();
                            }
                            try {
                                Thread.sleep(50);
                            } catch (Exception e) {}
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            Thread commandThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Scanner scanner = new Scanner(System.in);

                    while(true) {
                        String line = scanner.nextLine();

                        String[] spl = line.split("\\s+");

                        if(spl[0].equalsIgnoreCase("quit")) {
                            System.exit(0);
                        }

                        else if(spl[0].equalsIgnoreCase("listhits")) {
                            System.out.println(ServerList.pageVisits + " visits");
                        }

                        else if(spl[0].equalsIgnoreCase("update")) {
                            try {
                                Runtime.getRuntime().exec("rm Moxie.jar");
                                Runtime.getRuntime().exec("wget http://grappl.io:888/html/Moxie.jar");

                                for(Map.Entry<String, Socket> socketEntry :relayServers.entrySet()) {
                                    Socket socket = socketEntry.getValue();

                                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                                    PrintStream printStream = new PrintStream(dataOutputStream);
                                    printStream.println("UPDATE");
                                }

                                Runtime.getRuntime().exec("java -jar Moxie.jar");
                                System.exit(0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        else {
                            log("Unknown command!");
                        }
//                    else if(spl[0].equalsIgnoreCase("refresh")) {
//                        System.out.println("[CONSOLE] Attempting refresh");
//                    }
                    }
                }
            });
            commandThread.start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        relayControl = new ServerSocket(GrapplGlobal.RELAY_CONTROL);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    log("Listening for relay servers.");
                    while(true) {
                        try {
                            final Socket socket = relayControl.accept();
                            System.out.print("RELAY SERVER | " + socket.getInetAddress() + " IS CONNECTED");

                            String address = socket.getInetAddress().toString();
                            address = address.substring(1, address.length());
                            final String finalAddress = address;

                            final String AUS_IP = "128.199.98.169";
                            final String AMS_IP = "178.62.201.146";
                            final String SF_IP = "45.55.4.249";
                            final String NYC_IP = "104.236.194.63";

                            if(address.equalsIgnoreCase(AUS_IP)) {
                                relayServers.put("AUSTRALIA", socket);
                                relayMap.put("AUSTRALIA", true);
                                System.out.println(" AUSTRALIA");
                            } else if(address.equalsIgnoreCase(NYC_IP)) {
                                relayServers.put("NYC", socket);
                                relayMap.put("NYC", true);
                                System.out.println(" NYC");
                            }else if(address.equalsIgnoreCase(AMS_IP)) {
                                relayServers.put("AMS", socket);
                                relayMap.put("AMS", true);
                                System.out.println(" AMS");
                            } else {
                                System.out.println("");
                            }

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
//                                    while(true) {
                                        try {
                                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                                        while(true) {
                                            String read = dataInputStream.readLine();
//
                                            String[] spl = read.split("\\s+");

                                            int i = Integer.parseInt(spl[0]);

                                            if(i == 0) {
                                                log("Server opened at " + getCode(finalAddress));

                                                GrapplStats.totalHosts++;
                                                GrapplStats.currentHosts++;
                                            }

                                            if(i == 1) {
                                                log("Server closed at " + getCode(finalAddress));

                                                GrapplStats.currentHosts--;
                                            }

                                            if(i == 2) {
//                                                GrapplStats.currentHosts--;
                                            }

                                            if(i == 3) {
//                                                GrapplStats.currentHosts--;
                                            }

                                            Thread.sleep(20);
                                        }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
//                                    }
                                }
                            }).start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

    }

    public static int port = 40000;

    public static int getPort(User user, String ip) {
        if(user.getPort() != -1 && user.isAlphaTester()) {
            return user.getPort();
        } else {
            if(!isHead) {
                if(ipToPort.containsKey(ip)) {
                    if(ipToPort.get(ip) == -1) {
                        port += 2;
                        return port;
                    } else
                        return ipToPort.get(ip);
                } else {
                    Union union = Union.unionMap.get(ip);
                    if(union != null) {
                        if(System.currentTimeMillis() < (union.timeCreated + (60000 * 5))) {
                            return union.port;
                        }
                    }

                    while(portsTaken.contains(port)) {
                        port += 2;
                    }

                    port+=2;

                    return port - 2;
                }
            }
        }

        return 25565;
    }

    public static void openRelay() {

        try {
            Union.load();
        } catch (Exception e) {
            System.out.println("union error");
            e.printStackTrace();
        }

        for(int i : portsTaken) {
            System.out.println(i);
        }
        try {
            relaySocket = new Socket(GrapplGlobal.DOMAIN, GrapplGlobal.RELAY_CONTROL);
            System.out.println("Connected to head server..");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DataInputStream dataInputStream = new DataInputStream(relaySocket.getInputStream());
                        while (true) {
                            try {
                                String preface = dataInputStream.readLine();

                                if(preface.equalsIgnoreCase("INCOMING")) {
                                    String read = dataInputStream.readLine();
                                    String username = dataInputStream.readLine();
                                    log("MSG: " + read + " - " + username);

                                    String[] spl = read.split("\\s+");
                                    String ip = spl[0];
                                    int port = Integer.parseInt(spl[1]);

                                    ipToPort.put(ip, port);
                                } else if(preface.equalsIgnoreCase("UPDATE")) {
                                    Runtime.getRuntime().exec("rm Moxie.jar");
                                    Runtime.getRuntime().exec("wget http://grappl.io:888/html/Moxie.jar");
                                    Runtime.getRuntime().exec("java -jar Moxie.jar relay");
                                    System.exit(0);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();

                                relaySocket.close();

                                connectionLost = true;

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        while(true) {
                                            try {
                                                relaySocket = new Socket(GrapplGlobal.DOMAIN, GrapplGlobal.RELAY_CONTROL);
                                                relaySocket.close();

                                                openRelay();
                                                return;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            try {
                                                Thread.sleep(500);
                                            } catch (InterruptedException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                    }
                                }).start();

                                return;
                            }

                            Thread.sleep(50);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        startServer();
    }

    public static void startServer() {

        try {
            trafficServer = new ServerSocket(GrapplGlobal.INNER_TRANSIT);
            messageServer = new ServerSocket(GrapplGlobal.MESSAGE_PORT);
            heartBeatServer = new ServerSocket(GrapplGlobal.HEARTBEAT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Thread heartbeatReception = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        final Socket heartBeatClient = heartBeatServer.accept();

                        final String server = heartBeatClient.getInetAddress().toString();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    DataInputStream dataInputStream = new DataInputStream(heartBeatClient
                                                .getInputStream());
                                    while(true) {
                                        int time = dataInputStream.readInt();

                                        tickHost(server);

                                        try {
                                            Thread.sleep(50);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (IOException e) {
                                    try {
                                        hostMap.get(server).closeHost();
                                    } catch (Exception ignore) {

                                    }
                                }
                            }
                        }).start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        heartbeatReception.start();

        Thread commandThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);

                while(true) {
                    String line = scanner.nextLine();

                    String[] spl = line.split("\\s+");

                    if(spl[0].equalsIgnoreCase("quit")) {
                        shutdown();
                    }

                    else if (spl[0].equalsIgnoreCase("hosts")) {
                       log(hosts.size() + "");
                    }

                    else if(spl[0].equalsIgnoreCase("hostlist")) {
                        String output = hosts.size() + " host(s): ";

                        for (int i = 0; i < hosts.size(); i++) {
                            if(i != 0) {
                                output += " - ";
                            }
                            Host host = hosts.get(i);

                            output += host.getAddress() + ":" + host.getPortNumber();
                        }

                        log(output);
                    }

                    else if(spl[0].equalsIgnoreCase("debind")) {
                        int port = Integer.parseInt(spl[1]);

                        try {
                            for (int i = 0; i < hosts.size(); i++) {
                                Host host = hosts.get(i);

                                if(host.getPortNumber() == port) {
                                    host.closeHost();
                                }
                            }
                        } catch (Exception er) {
                            log("An error occurred.");
                        }
                    }

                    else if(spl[0].equalsIgnoreCase("detailed")) {
                        log("Switching to detailed mode.");
                        detailedDebug = true;
                    }

                    else if(spl[0].equalsIgnoreCase("undetailed")) {
                        log("Switching to undetailed mode.");
                        detailedDebug = false;
                    }

                    else if(spl[0].equalsIgnoreCase("userfor")) {
                        try {
                            Host host = getHost(Integer.parseInt(spl[1]));
                            log(host.getUser().getUsername());
                        } catch (Exception er) {
                            log("An error occurred, user likely doesn't exist.");
                        }
                    }

                    else {
                        log("Unknown command!");
                    }
//                    else if(spl[0].equalsIgnoreCase("refresh")) {
//                        System.out.println("[CONSOLE] Attempting refresh");
//                    }
                }
            }
        });
        commandThread.start();

        boolean isRunning = true;
        // Waiting for connections from hosts
        while(isRunning) {
            // Accept a host connection.
            try {
                final Socket hostSocket = messageServer.accept();

                // Getting of user login information will occur here

                String address = hostSocket.getInetAddress().toString();

                if(connectionsPerIP.containsKey(address)) {
                    int amount = connectionsPerIP.get(address);
                    amount++;
                    connectionsPerIP.put(address, amount);
                } else {
                    connectionsPerIP.put(address, 1);
                }

                if(connectionsPerIP.get(address) < 15) {
                    try {
                        Host host = new Host(hostSocket, userForIP(hostSocket.getInetAddress().toString()));
                        host.start();
                        addHost(host);
                    } catch (Exception e) {}
                }
            } catch (Exception e) {
                e.printStackTrace();
                isRunning = false;
            }
        }

        log("Fatal error. Closing.");
    }

    public static String serverForIP(InetAddress inetAddress) {
        System.out.println(inetAddress + " logged in");
        try {
            File database = new File("GeoLite2-Country.mmdb");
            DatabaseReader databaseReader = new DatabaseReader.Builder(database).build();

            CountryResponse countryResponse = databaseReader.country(inetAddress);

//            System.out.println(countryResponse);

            String continent = countryResponse.getContinent().toString();

            // NYC server
            if(continent.equalsIgnoreCase("North America")) return "n";
            if(continent.equalsIgnoreCase("South America")) return "n";

            // Amsterdam server
            if(continent.equalsIgnoreCase("Europe")) return "e";
            if(continent.equalsIgnoreCase("Africa")) return "e";

            // Singapore server
            if(continent.equalsIgnoreCase("Asia")) return "p";
            if(continent.equalsIgnoreCase("Oceania")) return "p";
            if(continent.equalsIgnoreCase("Antarctica")) return "p";
        } catch (Exception e) {
//            e.printStackTrace();
            return "n";
        }

        return "n";
    }

    public static User userForIP(String ip) {
        return UserManager.getUserByName(ipToUser.get(ip));
    }

    public static void addHost(Host host) {
        hosts.add(host);
        hostMap.put(host.getAddress(), host);
        portMap.put(host.getPortNumber(), host);
        heartBeats.put(host.getAddress(), System.currentTimeMillis());
    }

    public static void removeHost(Host host) {
        hosts.remove(host);
        portsTaken.remove(host.getPortNumber());
    }

    public static Host getHost(int port) {
        return portMap.get(port);
    }

    public static void tickHost(String ip) {
        heartBeats.put(ip, System.currentTimeMillis());
    }

    public static long getHostTick(String ip) {
        if(heartBeats.containsKey(ip)) {
            return heartBeats.get(ip);
        } else return System.currentTimeMillis();
    }

    public static boolean serverStatus(String region) {
        try {
            return relayMap.get(region);
        } catch (Exception e) {
            return false;
        }
    }

    public static int connectedHosts() {
        return hosts.size();
    }

    public static void log(String log) {
        String tag = DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
        System.out.println("[" + tag + "] " + log);
    }

    public static String getCode(String address) {
        final String AUS_IP = "128.199.98.169";
        final String AMS_IP = "178.62.201.146";
        final String SF_IP = "45.55.4.249";
        final String NYC_IP = "104.236.194.63";

        if(address.equalsIgnoreCase(AUS_IP)) return "AUSTRALIA";
        if(address.equalsIgnoreCase(AMS_IP)) return "AMS";
        if(address.equalsIgnoreCase(NYC_IP)) return "NYC";

        return "";
    }

    public static void shutdown() {
        Union.unionList.clear();;

        for(Host host : hosts) {
            Union union = new Union();
            union.username = host.getUser().getUsername();
            union.port = host.getPortNumber();
            union.address = host.getAddress();
        }

        Union.saveAll();

        System.exit(0);
    }
}
