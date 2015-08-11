package io.grappl.client.freezer;

import java.io.*;

public class IcePlayer {

    private long id;

    public IcePlayer(long id) {
        this.id = id;
        load();
    }

    public void load() {
        File input = new File("recv-" + id);
        try {
            FileInputStream fileInputStream = new FileInputStream(input);

            DataInputStream dataInputStream = new DataInputStream(fileInputStream);

            try {
                long time = dataInputStream.readLong();
//                byte[] data = dataInputStream.readByte()
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
