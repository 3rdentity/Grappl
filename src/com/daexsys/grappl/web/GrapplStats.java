package com.daexsys.grappl.web;

import com.daexsys.grappl.GrapplServerState;
import com.daexsys.grappl.server.Host;
import com.daexsys.grappl.server.Server;
import com.daexsys.grappl.server.UserManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GrapplStats implements HttpHandler {
    public static int totalHosts = 0;
    public static int currentHosts = 0;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if(UserManager.getUser(httpExchange).getUsername().equalsIgnoreCase("Cactose")) {
            StringBuilder response = new StringBuilder("");
            response.append(WebServer.getTailoredTop(httpExchange));
            response.append("<body bgcolor = '292F54'>");
            response.append("<font size = '6' color = 'ffffff'>");
            response.append("number of registered users: "+ UserManager.numberOfAccounts() + "<br></center>");
            response.append(currentHosts + " hosts online<p>");
            response.append(totalHosts + " hosts total<p>");

            String hosts = "";
            Set<String> addresses = new HashSet<String>();

            for (int i = 0; i < Server.hosts.size(); i++) {
                Host host = Server.hosts.get(i);
                if(!addresses.contains(host.getAddress())) {
                    hosts += host.getAddress() + " | Time: " + host.getTimeOpened() + " | Connections: " + host.connectionCount() + " | Port: " + host.getPortNumber() + "<br>";
                    addresses.add(host.getAddress());
                }
            }
            response.append(hosts);

            httpExchange.sendResponseHeaders(200, response.length());
            httpExchange.getResponseBody().write(response.toString().getBytes());
            httpExchange.getResponseBody().close();
        }
    }

    public static long getUptime() {
        return System.currentTimeMillis() - GrapplServerState.timeStarted;
    }
}
