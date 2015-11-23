package test;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;
import io.grappl.client.impl.error.RelayServerNotFoundException;
import io.grappl.client.impl.stable.tcp.TCPGrappl;

import java.util.HashSet;
import java.util.Set;

public class ConnectionTest {

    public static void main(String[] args) {

        Set<String> relays = new HashSet<String>();
        relays.add("n.grappl.io");
        relays.add("s.grappl.io");
        relays.add("e.grappl.io");
        relays.add("p.grappl.io");

        boolean failure = false;

        /* Test as anonymous */
        for(String s : relays) {
            Grappl grappl = new TCPGrappl(Application.getApplicationState());
            grappl.getInternalServer().setPort(-1);
            boolean success = false;
            try {
                success = grappl.connect(s);
            } catch (RelayServerNotFoundException e) {
                e.printStackTrace();
            }

            if(!success) {
                failure = true;
            }

            System.out.println(success + " " + grappl);
        }
        System.out.println("Overall result: success=" + !failure);
    }
}
