package io.grappl.server.relay;

import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class User {
    private int port = 40000;

    private String username;
    private String password;
    private String instanceId;

    private boolean alphaTester = false;
    private boolean fixedPort = false;

    private List<String> bannedIps = new ArrayList<String>();

    public int blocksIn = 0;
    public int blocksOut = 0;
    public int connectionsTotal = 0;

    public long timeRegistered = 0;

    private long timeLastSeen;

    public User(String username, String password, int port) {
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public boolean isAlphaTester() {
        return alphaTester;
    }

    public void setAlphaTester(boolean alphaTester) {
        this.alphaTester = alphaTester;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public void setFixedPort(int port) {
            this.port = port;
            fixedPort = true;
//        }
    }

    public boolean isFixedPort() {
        return fixedPort;
    }

    public boolean attemptLogin(String username, String password) {
        if(username.equalsIgnoreCase(username)) {
            if(password.equalsIgnoreCase(password)) {
                UserManager.loginUser(this, "");
                return true;
            }
        }

        return false;
    }

    public void save() {
        Gson gson = new Gson();

        File file = new File("profiles/");
        file.mkdirs();
        String json = gson.toJson(this);
        File playerDat = new File("profiles/" + getUsername() + ".dat");

        try {
            playerDat.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            PrintStream printStream = new PrintStream(new FileOutputStream(playerDat));
            printStream.println(json);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static User load(String username) {
        Gson gson = new Gson();

        File file = new File("profiles/");
        file.mkdirs();
        String loaded = "";
        User person = null;

        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream("profiles/"+username+".dat"));
            loaded = dataInputStream.readLine();
            person = gson.fromJson(loaded, User.class);
        } catch (IOException e) {
        }

        return person;
    }

    public int getPort() {
        return port;
    }

    public void banIP(String ip) {
        if(!bannedIps.contains(ip)) {
            bannedIps.add(ip);
        }
    }

    public void pingTimeLastSeen() {
        timeLastSeen = System.currentTimeMillis();
    }

    public void unbanIP(String ip) {
        bannedIps.remove(ip);
    }

    public boolean isIPBanned(String ip) {
        return bannedIps.contains(ip.substring(1, ip.length()));
    }
}
