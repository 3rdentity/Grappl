package com.daexsys.grappl.web.list;

import com.daexsys.grappl.server.Server;
import com.daexsys.grappl.server.UserManager;
import com.daexsys.grappl.web.GrapplStats;
import com.daexsys.grappl.web.WebServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class ServerStatus implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Stats request received");

        try {
            StringBuilder response = new StringBuilder("");
            response.append(WebServer.getTailoredTop(httpExchange));
            response.append(WebServer.getTop());
            try {

                response.append("<h1>Grappl Stats</h1>");
                response.append(UserManager.numberOfAccounts() + " registered accounts<br>");
                response.append(GrapplStats.currentHosts + " Grappls currently online");
                response.append("<p>");

                response.append("<h1>Relay Server Status</h1>");
                response.append("<hr>");
                response.append("NYC Status (North America, South America): " + (Server.serverStatus("NYC") ? "online" : "offline"));


                response.append("<hr>");
                response.append("Amsterdam Status (Europe, Africa): " + (Server.serverStatus("AMS") ? "online" :
                        "offline" +
                        ""));
                response.append("<hr>");
                response.append("Singapore Status (Oceania, Asia, Antarctica): " + (Server.serverStatus("AUSTRALIA") ? "online" : "offline"));
                response.append("<hr>");
            } catch (Exception e) {
                e.printStackTrace();
            }

            httpExchange.sendResponseHeaders(200, response.length());
            httpExchange.getResponseBody().write(response.toString().getBytes());
            httpExchange.getResponseBody().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
