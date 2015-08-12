package io.grappl.server.relay;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserManager {

    private static Map<String, User> users = new HashMap<String, User>();
    private static Map<String, User> ids = new HashMap<String, User>();
    private static Set<User> userss = new HashSet<User>();

    static {
        try {
            // load
            File file = new File("profiles/");

            for (File file2 : file.listFiles()) {
                try {
                    User person=null;

                    Gson gson = new Gson();
                    try {
                        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file2));
                        String loaded = dataInputStream.readLine();

                        try {
                            person = gson.fromJson(loaded, User.class);
                        } catch (Exception ignore) {}

                        if(person != null) {
                            addUser(person.getUsername(), person);

                            if (person.isAlphaTester()) {
                                Server.portsTaken.add(person.getPort());
                            }
                        }

                    } catch (IOException e) {
                        System.out.println("Not found");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loginUser(User user, String id) {
        user.setInstanceId(id);
        ids.put(user.getInstanceId()+"", user);
    }

    public static void addUser(String name, User user) {
        users.put(name, user);
        userss.add(user);
    }

    public static User getUserByName(String name) {
//        System.out.println("NAME: " + name);/
        if(users.containsKey(name)) {
            return users.get(name);
        } else {
            User us = User.load(name);

            if(us == null) {
                return new User("ERROR", "ERROR".hashCode() + "", -1);
            }
            return us;
        }
    }

    public static User getUserById(String id) {

//        System.out.println("ID: " + id);
        if(ids.containsKey(id)) {
//            System.out.println("found");
            return ids.get(id);
        } else return new User("ERROR", "ERROR".hashCode()+"", -1);
    }

    public static User getUser(HttpExchange httpExchange) {
        String cookie = httpExchange.getRequestHeaders().getFirst("Cookie");
        String ip = httpExchange.getRemoteAddress().toString();

        Map<String, String> cookieParams = new HashMap<String, String>();

        try {
            if(!cookie.equalsIgnoreCase("")) {

                String[] cookieTag = cookie.split("\\;");

                for (String string : cookieTag) {
                    String[] pair = string.split("\\=");

                    String key = pair[0].replaceAll(" ", "");
                    String value = pair[1].replaceAll(" ", "");

                    cookieParams.put(key, value);
                }
            }else {
                System.out.println("empty cookie");
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }

        User user = getUserById(cookieParams.get("id"));

        if(user != null) {
            if (!user.getUsername().equalsIgnoreCase("ERROR")) {
//                System.out.println("Linking user with " + relayServerIP);
                Server.ipToUser.put(ip.split("\\:")[0], user.getUsername());
            }
            return user;
        }

        return new User("ERROR", "ERROR".hashCode()+"", -1);

    }

    public static int getAlphaTesterCount() {
        int alphaTesters = 0;

        try {
            for (User user : userss) {
                if (user.isAlphaTester()) alphaTesters++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return alphaTesters;
    }

    public static Set<User> getUserss() {
        return userss;
    }

    public static int numberOfAccounts() {
        return users.size();
    }

    public static boolean personExists(String name) {
        return users.containsKey(name);
    }
}
