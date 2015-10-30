package io.grappl.client.gui;

import io.grappl.client.api.Grappl;

import java.util.ArrayList;
import java.util.List;

public class MultiLauncher {

    private List<Grappl> startedGrappls = new ArrayList<Grappl>();

    public void addNewGrappl(Grappl grappl) {
        startedGrappls.add(grappl);
    }

    public void removeGrappl(Grappl grappl) {
        startedGrappls.remove(grappl);
    }

    public int numberOfGrappls() {
        return startedGrappls.size();
    }

    public Grappl getGrapplByPort(int portNumber) {
        for(Grappl grappl : startedGrappls) {
            if(grappl.getInternalPort() == portNumber) {
                return grappl;
            }
        }

        return null;
    }
}
