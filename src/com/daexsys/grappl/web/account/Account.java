package com.daexsys.grappl.web.account;

import com.daexsys.grappl.server.User;
import com.daexsys.grappl.server.UserManager;
import com.daexsys.grappl.web.WebServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class Account implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        StringBuilder response = new StringBuilder();

        response.append(WebServer.getTailoredTop(httpExchange));
        response.append(WebServer.getTop());

        User user = UserManager.getUser(httpExchange);
        int port = user.getPort();

        boolean error = false;

        try {
            java.net.URI uri = httpExchange.getRequestURI();
            String[] spl = uri.toString().split("\\?");
            String[] parts = spl[1].split("\\&");
            String name = parts[0].split("\\=")[1];
        } catch (Exception e) {
            error = true;
        }

        response.append("<div align ='left'>");
        response.append("<font color = 'ffffff'>");
        StringBuilder message = new StringBuilder("Welcome, " + user.getUsername() + "!</h3>");
        response.append(WebServer.header(message.toString()));
        if(user.isAlphaTester()) response.append("<font color = 'lime'>");
        if(user.isAlphaTester()) response.append("Alpha tester: " + user.isAlphaTester() + ".");
        if(user.isAlphaTester()) response.append(" Premium time left: Infinite<p>");
        if(user.isAlphaTester()) response.append("</font>");
//        response.append(WebServer.header("Static ports"));



        if(user.isAlphaTester()) {
            response.append("<form action = 'setport' method = 'post'>");
            if (port == 40000) {
                response.append("Your port is randomized every time you connect.");
            } else {
                if(user.getPort() != -1) {
                    response.append("Your static port: " + user.getPort() + "<p>");
                } else {
                    response.append("You have not set a static port yet, so your port will always be random.<br>");
                }
            }

            if(!error) {
                response.append("<font color = 'red'>Port unavailable</font><br>");
            }
            response.append("Change port: <input type = 'text' name = 'port'>");
            response.append(" <input type = 'submit' value = 'Update'>");
            response.append("</form>");
        } else {
            response.append("<p>You are not an alpha tester, so you cannot set a static port.<br><a style='color:#D1FFDE' href = '/donate'>Become an alpha tester (donate).</a>");
        }

        response.append("<hr>");
        response.append(WebServer.header("Stats"));
        response.append("Connections routed: " + user.connectionsTotal);
        response.append("<br>");
        response.append("Blocks routed in: " + user.blocksIn);
        response.append("<br>");
        response.append("Blocks routed out: " + user.blocksOut);
        response.append("<hr>");
//        response.append("<a href = 'http://grappl.io:888/html/GrapplAutoUpdater.jar'>Download unstable autoupdating client (more features and better quality connection to Oceania/Asia)</a>");

        httpExchange.sendResponseHeaders(200, response.length());
        httpExchange.getResponseBody().write(response.toString().getBytes());
        httpExchange.getResponseBody().close();
    }

    // pp button
//    <form action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_top">
//    <input type="hidden" name="cmd" value="_s-xclick">
//    <input type="hidden" name="encrypted" value="-----BEGIN PKCS7-----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-----END PKCS7-----
//            ">
//    <input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_buynowCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
//    <img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">
//    </form>

}
