package io.grappl.client.freezer;

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
        File receive = new File("recv-" + freezerID + ".frz");
        try {
            receive.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(receive);
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

            for(TemporalBlock temporalBlock : receivedBlocks) {
                dataOutputStream.writeLong(temporalBlock.timeActive);
                fileOutputStream.write(temporalBlock.data);
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

            for(TemporalBlock temporalBlock : sentBlocks) {
                dataOutputStream.writeLong(temporalBlock.timeActive);
                fileOutputStream.write(temporalBlock.data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
