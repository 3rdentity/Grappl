package com.daexsys.grappl.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class Terms implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        StringBuilder response = new StringBuilder("");
        response.append(WebServer.getTailoredTop(httpExchange));
        response.append(WebServer.getTop());

        response.append("Do not use Grappl to do anything illegal. You are responsible for what you use this service for.<br>");
        response.append("Also, don't use Grappl to transmit any copyrighted material that you do not have the right to transmit.<br>");
        response.append("<p>");
        response.append("There is no uptime guarantee.");

        httpExchange.sendResponseHeaders(200, response.length());
        httpExchange.getResponseBody().write(response.toString().getBytes());
        httpExchange.getResponseBody().close();
    }
}
