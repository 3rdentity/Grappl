package com.daexsys.grappl.web.cgi;

import com.daexsys.grappl.server.UserManager;
import com.daexsys.grappl.web.WebServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class PremiumUp implements HttpHandler{
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if(UserManager.getUser(httpExchange).getUsername().equalsIgnoreCase("Cactose")) {
            StringBuilder response = new StringBuilder("");
            response.append(WebServer.getTailoredTop(httpExchange));
            response.append("<body bgcolor = '292F54'>");

            response.append("<form action = '/p2' method='post'>");
            response.append("<input type = 'text' name = 'person'>");
            response.append("<input type = 'submit'>");
            response.append("</form>");

            httpExchange.sendResponseHeaders(200, response.length());
            httpExchange.getResponseBody().write(response.toString().getBytes());
            httpExchange.getResponseBody().close();
        }
    }
}
