package test;

import io.grappl.client.Application;
import io.grappl.client.api.Grappl;

import java.util.HashSet;
import java.util.Set;

public class ConnectionTest {

    public static void main(String[] args) {

        Application.create(null);

        Set<String> relays = new HashSet<String>();
        relays.add("n.grappl.io");
        relays.add("s.grappl.io");
        relays.add("e.grappl.io");
        relays.add("p.grappl.io");

        for(String s : relays) {
            Grappl grappl = new Grappl();
            boolean success = grappl.connect(s);
            System.out.println(success + " " + s + " " + grappl.getRelayServer());
        }
    }
}
