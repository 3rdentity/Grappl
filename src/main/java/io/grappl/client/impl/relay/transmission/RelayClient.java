package io.grappl.client.impl.relay.transmission;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class RelayClient {

    private static RelayTransmission getFromMsg(String webLocation) {

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

    public static void main(String[] args) {
        RelayTransmission relayTransmission = RelayTransmission.getRelayTransmissionFrom("http://grappl.io:888/relays.json");

        System.out.println("Amount: " + relayTransmission);
    }
}
