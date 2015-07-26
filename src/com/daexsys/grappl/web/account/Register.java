package com.daexsys.grappl.web.account;

import com.daexsys.grappl.web.WebServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class Register implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

StringBuilder response = new StringBuilder();

            response.append(WebServer.getTailoredTop(httpExchange));
response.append(
        WebServer.getTop()+"<center>" +
        "<font size = '6'>Create Account</font>" +

        "<form action='registerhandler' method='post'>" +
        "<input type = 'text' placeholder = 'username' name='user'><br>" +
        " <input type = 'password' placeholder = 'password' name='pass'><br>" +
        " <input type = 'password' placeholder = 'confirm password' name='pass2'><br>" +
        "<input type = 'submit' value='Register'>" +
        "</form>");

        httpExchange.sendResponseHeaders(200, response.length());
        httpExchange.getResponseBody().write(response.toString().getBytes());
        httpExchange.getResponseBody().close();
        }
        }
