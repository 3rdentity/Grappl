package com.daexsys.grappl.web.account;

import com.daexsys.grappl.web.WebServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class Login implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        StringBuilder response = new StringBuilder();

        response.append(WebServer.getTailoredTop(httpExchange));
        response.append(
                "<html>"+ WebServer.getTop()+"<center>" +
                        "<font size = '6'>Login</font>" +

                "<form action='auth' method='post'>" +
                "<input type = 'text' placeholder = 'username' name='user'><br>" +
                " <input type = 'password' placeholder = 'password' name='pass'><br>" +
                "<input type = 'submit' value='Login'>" +
                "</form>");

        httpExchange.sendResponseHeaders(200, response.length());
        httpExchange.getResponseBody().write(response.toString().getBytes());
        httpExchange.getResponseBody().close();
    }
}
