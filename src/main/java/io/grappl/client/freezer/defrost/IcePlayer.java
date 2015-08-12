package io.grappl.client.freezer.defrost;

import io.grappl.client.freezer.ReplayBlock;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class IcePlayer {

    private long id;

    public IcePlayer(long id) {
        this.id = id;
        load();
    }

    public static void main(String[] args) {
        IcePlayer icePlayer = new IcePlayer(1439322393836l);
//        icePlayer.load();
    }

    public void load() {
        final List<ReplayBlock> replayBlockList = new ArrayList<ReplayBlock>();

        File input = new File("recv-" + id +".frz");
        try {
            FileInputStream fileInputStream = new FileInputStream(input);

            DataInputStream dataInputStream = new DataInputStream(fileInputStream);

            long num = dataInputStream.readLong();

            while(num > 0) {

                try {
                    long time = dataInputStream.readLong();
                    short length = dataInputStream.readShort();
                    byte[] bytes = new byte[4096];

                    int place = 0;
                    while (length > 0) {
                        String line = dataInputStream.readLine();

                        bytes[place] = Byte.parseByte(line);

                        place++;
                        length--;
                    }

                    ReplayBlock replayBlock = new ReplayBlock(time, bytes);
                    replayBlockList.add(replayBlock);
                } catch (IOException e) {
                    break;
                }

                num--;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("RCV " + replayBlockList.size());

        List<ReplayBlock> sentBlockList = new ArrayList<ReplayBlock>();
        input = new File("send-" + id+".frz");
        try {
            FileInputStream fileInputStream = new FileInputStream(input);

            DataInputStream dataInputStream = new DataInputStream(fileInputStream);

            long num = dataInputStream.readLong();

            while(num > 0) {

                try {
                    long time = dataInputStream.readLong();
                    short length = dataInputStream.readShort();
                    byte[] bytes = new byte[4096];

                    int place = 0;
                    while (length > 0) {
                        String line = dataInputStream.readLine();

                        bytes[place] = Byte.parseByte(line);

                        place++;
                        length--;
                    }

                    ReplayBlock replayBlock = new ReplayBlock(time, bytes);
                    sentBlockList.add(replayBlock);
                } catch (IOException e) {
//                    e.printStackTrace();
                    break;
                }

                num--;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("SBL " + sentBlockList.size());

        try {
            ServerSocket serverSocket = new ServerSocket(25666);

            System.out.println("Started server");

            final long timeStarted = System.currentTimeMillis();

            final Socket socket = serverSocket.accept();
            System.out.println("Received client");

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        long timeElapsed = System.currentTimeMillis() - timeStarted;

                        if(timeElapsed > replayBlockList.get(0).time) {
                            try {
                                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                                for (byte bite : replayBlockList.get(0).data) {
                                    try {
                                        dataOutputStream.writeByte(bite);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {}

                            replayBlockList.remove(replayBlockList.get(0));
                        }
                    }
                }
            });
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
