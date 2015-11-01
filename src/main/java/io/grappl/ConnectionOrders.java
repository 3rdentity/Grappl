package io.grappl;

import java.util.Arrays;
import java.util.List;

/**
 * Order that relay servers should be attempted by clients in a given location.
 */
public class ConnectionOrders {
//
//    static final String EAST_COAST_NA_RELAY = "n.grappl.io";
//    static final String EUROPE_RELAY = "e.grappl.io";
//    static final String WEST_COAST_NA_RELAY = "s.grappl.io";
//    static final String PACIFIC_RELAY = "p.grappl.io";

    private List<String> nycOrder =     Arrays.asList("n.grappl.io", "s.grappl.io", "e.grappl.io", "p.grappl.io");
    private List<String> sFOrder =      Arrays.asList("s.grappl.io", "n.grappl.io", "e.grappl.io", "p.grappl.io");
    private List<String> europeOrder =  Arrays.asList("e.grappl.io", "n.grappl.io", "p.grappl.io", "s.grappl.io");
    private List<String> pacificOrder = Arrays.asList("p.grappl.io", "s.grappl.io", "n.grappl.io", "e.grappl.io");

    public List<String> getNewYorkOrder() {
        return nycOrder;
    }

    public List<String> getSFBayOrder() {
        return sFOrder;
    }

    public List<String> getEuropeOrder() {
        return europeOrder;
    }

    public List<String> getPacificOrder() {
        return pacificOrder;
    }
}
