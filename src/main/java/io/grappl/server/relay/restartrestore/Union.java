package io.grappl.server.relay.restartrestore;

import io.grappl.server.relay.Server;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Union {
    public static List<Union> unionList = new ArrayList<Union>();
    public static Map<String, Union> unionMap = new HashMap<String, Union>();

    public String username;
    public String address;
    public int port;
    public long timeCreated = System.currentTimeMillis();

    public Union() {
        unionList.add(this);
    }

    public static void saveAll() {
        File unions = new File("unions.dat");

        try {
            unions.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintStream printStream = null;
        try {
            printStream = new PrintStream(new FileOutputStream(unions));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for(Union union : unionList) {
            Gson gson = new Gson();

//            File file = new File("unions.dat");
//            file.mkdirs();
            String json = gson.toJson(union);

            try {
                printStream.println(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void load() {
        Gson gson = new Gson();

        File file = new File("unions.dat");
        String loaded = "laaa";
        Union person = null;

        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream("unions.dat"));

            while(loaded != null) {
                loaded = dataInputStream.readLine();
                person = gson.fromJson(loaded, Union.class);
                unionList.add(person);
                unionMap.put(person.address, person);
//                System.out.println("loaded union: " + person.port);
                Server.portsTaken.add(person.port);
            }
        } catch (IOException e) {
//            System.out.println("Not found");
        }
    }
}
