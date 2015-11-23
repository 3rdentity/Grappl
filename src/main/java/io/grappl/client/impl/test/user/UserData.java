package io.grappl.client.impl.test.user;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A snapshot of information related to a user.
 * This can be fetched from the grappl core in json form.
 */
public class UserData {

    /* The person's unique ID. */
    private UUID uuid;

    /* The person's current username. */
    private String username;

    /* Whether or not the user has a premium account. */
    private boolean isPremium;

    /* A list of all the static ports available to the user. */
    private Set<Integer> staticPorts = new LinkedHashSet<Integer>();

    /* Data related statistics related to this account. */
    private DataStats dataStats;

    public UUID getUUID() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public Set<Integer> getStaticPorts() {
        return staticPorts;
    }

    public DataStats getDataStats() {
        return dataStats;
    }
}
