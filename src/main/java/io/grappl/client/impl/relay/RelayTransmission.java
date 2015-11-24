package io.grappl.client.impl.relay;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class RelayTransmission {

    private List<RelayServer> relayServerList = new ArrayList<RelayServer>();

    public void addRelayServer(RelayServer relayServer) {
        relayServerList.add(relayServer);
    }

    public List<RelayServer> getRelayServerList() {
        return relayServerList;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static RelayTransmission getRelayTransmissionFrom(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, RelayTransmission.class);
    }

    public static RelayTransmission getFromWebLocation(String webLocation) {
        try {
            URL url = new URL(webLocation);
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            String json = dataInputStream.readLine();
            return RelayTransmission.getRelayTransmissionFrom(json);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
