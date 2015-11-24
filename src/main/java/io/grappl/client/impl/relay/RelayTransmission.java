package io.grappl.client.impl.relay;

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
//        Gson gson = new Gson();
//        return gson.toJson(this);
        return null;
    }

    public static RelayTransmission getRelayTransmissionFrom(String json) {
//        Gson gson = new Gson();
//        return gson.fromJson(json, RelayTransmission.class);
        return null;
    }

    public static RelayTransmission getFromWebLocation(String webLocation) {
//        try {
//            URL url = new URL(webLocation);
//            URLConnection urlConnection = url.openConnection();
//            InputStream inputStream = urlConnection.getInputStream();
//            DataInputStream dataInputStream = new DataInputStream(inputStream);

            String json = "{\"relayServerList\":[{\"relayLocation\":\"n.grappl.io\",\"description\":\"East Coast NA\",\"ping\":44,\"up\":true},{\"relayLocation\":\"s.grappl.io\",\"description\":\"West " +
                    "Coast NA\",\"ping\":99,\"up\":true},{\"relayLocation\":\"e.grappl.io\",\"description\":\"Europe\",\"ping\":106,\"up\":true},{\"relayLocation\":\"p.grappl.io\",\"description\":\"Aus \n" +
                    "/ Oceania\",\"ping\":275,\"up\":true}]}";
            return RelayTransmission.getRelayTransmissionFrom(json);
//
//        }
//
//        catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        return null;
    }
}
