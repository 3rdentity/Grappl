package com.daexsys.grappl.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class FAQ implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        StringBuilder response = new StringBuilder("");
        response.append(WebServer.getTailoredTop(httpExchange));
        response.append(WebServer.getTop());

        response.append("<div align ='left' style='color:#FFFFFF'>");
        response.append("<h1>Frequently Asked Questions</h1>");

        response.append("<b>- Why?</b>");
        response.append("<br>");
        response.append("Because I want to make it easier for people to host servers (particularly game servers), play with friends, and connect with others." +
                " I've been particularly frustrated with the state of server hosting, you either have to mess around with port-fowarding (difficult, many routers" +
                " can't do it) or pay a company $10-$20 a month to host a server. With Grappl, you can put up a server with no hassle, running off your own machine," +
                " in a moment.");
        response.append("<p>");

        response.append("<b>- Is it safe?</b>");
        response.append("<br>");
        response.append("It's safe. Nothing nefarious is done with data sent through Grappl.<br>");
        response.append("You can also view the source code of the client here: <a style='color:#ffffff' href = 'https://github.com/Cactose/Grappl'>https://github.com/Cactose/Grappl</a>");

        response.append("<p>");

        response.append("<b>- What applications/games will it run with?</b>");
        response.append("<br>");
        response.append("It will run on any game that has a client which accepts alternate ports from the default.<br>");
        response.append("This includes Minecraft, Starbound, Terraria, and most other games. It can even be used to host HTTP servers!");

        response.append("<p>");

        response.append("<b>- How do you pay to keep this running?</b>");
        response.append("<br>");
        response.append("Donations, and donations alone! Please <a style='color:#ffffff' href = '/donate'>donate</a> if you like Grappl. You can alpha test new features if you do!");

        response.append("<p>");

        response.append("<b>- How can I set up a MC server with Grappl?</b><br>");
        response.append("This video does a great job of explaining how. The explanation starts at 1:10.<br>");
        response.append("<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/VJ5DisIQ5g4\" " +
                "frameborder=\"0\" allowfullscreen></iframe>");

        response.append("<p>");

        response.append("<b>- It won't work on my computer, what's wrong?</b><br>");
        response.append("If you don't have Java installed, you need to have that installed.<br>");
        response.append("If the application starts, you've let it through your firewall, and you");
        response.append(" get a public address (but you can't connect to it) any number of things could");
        response.append(" be wrong, but it's usually just the wrong port was entered.<br>");
        response.append("If it's being a total pain, you can contact me at <a style='color:#ffffff' href = 'https://twitter.com/Cactose'>https://twitter.com/Cactose</a> and ");
        response.append("I'll look into helping you.");

        httpExchange.sendResponseHeaders(200, response.length());
        httpExchange.getResponseBody().write(response.toString().getBytes());
        httpExchange.getResponseBody().close();
    }
}
