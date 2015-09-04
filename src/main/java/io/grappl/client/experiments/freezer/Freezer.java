package io.grappl.client.experiments.freezer;

import io.grappl.client.ClientLog;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Alpha feature designed to record network data and save it locally to play back interactions later.
 *
 * It will be activated with 'record' and 'derecord' commands. Or something like that.
 *
 * Note: to prevent misinterpretations, this doesn't send any data to the Grappl servers. It's just
 * to allow users to voluntarily save local recordings.
 */
public class Freezer {

    public final long freezerID = System.currentTimeMillis();

    private List<TemporalBlock> sentBlocks = new ArrayList<TemporalBlock>();
    private List<TemporalBlock> receivedBlocks = new ArrayList<TemporalBlock>();

    public Freezer() {
        ClientLog.log("Freezer active: id: " + freezerID);
    }

    public void sendBlock(byte[] data) {
        TemporalBlock temporalBlock = new TemporalBlock(this, data);
        sentBlocks.add(temporalBlock);
    }

    public void receiveBlock(byte[] data) {
        TemporalBlock temporalBlock = new TemporalBlock(this, data);
        receivedBlocks.add(temporalBlock);
    }

    public void save() {
        System.out.println("SAVING: SENT: " + sentBlocks.size());
        System.out.println("SAVING: RECEVIED: " + receivedBlocks.size());

        File receive = new File("recv-" + freezerID + ".frz");
        try {
            receive.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(receive);
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            PrintStream printStream = new PrintStream(dataOutputStream);

            dataOutputStream.writeLong(receivedBlocks.size());

            for(TemporalBlock temporalBlock : receivedBlocks) {
                dataOutputStream.writeLong(temporalBlock.timeActive);
                dataOutputStream.writeShort(temporalBlock.data.length);

                for (int i = 0; i < temporalBlock.data.length; i++) {
                    printStream.println(temporalBlock.data[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        File send = new File("send-" + freezerID + ".frz");
        try {
            send.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(send);
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            PrintStream printStream = new PrintStream(dataOutputStream);

            dataOutputStream.writeLong(sentBlocks.size());

            for(TemporalBlock temporalBlock : sentBlocks) {
                dataOutputStream.writeLong(temporalBlock.timeActive);
                dataOutputStream.writeShort(temporalBlock.data.length);

                for (int i = 0; i < temporalBlock.data.length; i++) {
                    printStream.println(temporalBlock.data[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
