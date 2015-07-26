package com.daexsys.grappl.client;

import com.daexsys.grappl.client.api.Grappl;
import com.daexsys.grappl.client.api.GrapplBuilder;

public class LoginTest {
    public static void main(String[] args) {

        GrapplBuilder grapplBuilder = new GrapplBuilder();
        Grappl grappl = grapplBuilder.useLoginDetails("cactose", ("hashbrown".hashCode()+"").toCharArray()).login().build();
        grappl.connect("127.0.0.1");
    }
}
