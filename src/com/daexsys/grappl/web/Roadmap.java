package com.daexsys.grappl.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class Roadmap implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        StringBuilder response = new StringBuilder("");

        try {
            response.append(WebServer.getTailoredTop(httpExchange));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            response.append(WebServer.getTop());
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.append("<div align ='left' style='color:#FFFFFF'>");
        response.append("<h1>Development Roadmap (Subject to change)</h1>");
        response.append("Grappl is a tool designed to make it easier to host servers, particularly game servers, without hassle." +
                " No matter who you are, even if you're on cell phone internet, you can quickly put up a server.<p>");

        response.append("Grappl is currently in alpha. Not early alpha, in fact, it's rapidly nearing beta. The service is free," +
                " and will be free as long as it lives. It's currently supported by donations. If you donate, you get to be" +
                " an alpha tester- you get access to features long before everyone else does. It's a thank you for keeping Grappl" +
                " up a bit longer." +
                "" +
                "At the moment, I'm mostly getting the technical side of Grappl straightened out. Grappl is run by one person" +
                " (Matt Hebert/me). I'm a college student without much money and I'm putting quite a bit of time into this.<p>" +
                "Grappl started out with only one server, located in NYC, which was pretty shoddy for folks over in Australia and Europe." +
                " The main challenge now is to expand Grappl's relay server network across the world without compromising the synchronization" +
                " of those servers. There is occasionally instability due to this. There are three relay server, one in NYC, one in Europe, and one" +
                " in Singapore. The plan is to make more if demand grows." +
                "<p>" +
                "Once everything is stable, Grappl will move into beta. Static ports, by this point, should be stable, and will no longer" +
                " be an alpha tester feature. People will be able to reserve static ports for $3 a month (subject to change). Current alpha testers" +
                " will have this free, forever." +
                "<p>");

        httpExchange.sendResponseHeaders(200, response.length());
        httpExchange.getResponseBody().write(response.toString().getBytes());
        httpExchange.getResponseBody().close();
    }
}
