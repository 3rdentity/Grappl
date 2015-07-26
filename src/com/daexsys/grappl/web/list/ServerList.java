package com.daexsys.grappl.web.list;

import com.daexsys.grappl.web.WebServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerList implements HttpHandler {

    public static List<String> hostsLookingForPlayers = new ArrayList<String>();

    public static int pageVisits = 0;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        pageVisits++;

        StringBuilder response = new StringBuilder("");
        response.append(WebServer.getTailoredTop(httpExchange));
        response.append(WebServer.getTop());
        try {

            response.append(WebServer.header("Servers looking for players"));
            response.append("<div align = 'left' style='color:#ffffff'><font color = white><p>");

            for (int i = hostsLookingForPlayers.size() - 1; i >= 0; i--) {
                String host = hostsLookingForPlayers.get(i);

                if(!host.contains("<")) {
                    response.append((i+1) + ". " +host + "<hr>");
                }
            }

            if(hostsLookingForPlayers.isEmpty()) {
                response.append("No servers are currently looking for players!<br>");
            }

            response.append("<p>You can add your server to this list with the <b>listadd [app/game name]</b> command in Grappl, or using the <b>request random visitors</b>.");

            response.append("");
        } catch (Exception e) {
            e.printStackTrace();
        }

        httpExchange.sendResponseHeaders(200, response.length());
        httpExchange.getResponseBody().write(response.toString().getBytes());
        httpExchange.getResponseBody().close();

    }

    public static List<String> getHostsLookingForPlayers() {
        return hostsLookingForPlayers;
    }

    public static void addHost(String toBeAdded) {
        hostsLookingForPlayers.add(toBeAdded);
    }

    public static void removeHost(String toBeAdded) {
        hostsLookingForPlayers.remove(toBeAdded);
    }
}
