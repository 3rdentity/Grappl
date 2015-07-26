package com.daexsys.grappl.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class GrapplDonate implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        StringBuilder response = new StringBuilder("");

        response.append(WebServer.getTailoredTop(httpExchange) + WebServer.getTop());
        response.append("<center>");
        response.append("<font size = '4' color = 'ffffff'>");
//        response.append("grappl currently costs ~$13 a month <i>just to keep online</i>,<br>not to mention all the effort that's put into developing and maintaining it<p>");
        response.append("You can donate any amount you want, any help is appreciated!<p>");
        response.append("If you donate $5 or more and include your username<br>");
        response.append("you will be able to alpha test new features, like static ports, which<br>");
        response.append("let you keep the same address every time you start Grappl.<br>");
        response.append("<small><small>(Alpha tester status has to be applied manually, so it will take a small amount of time to show up.)</small></small><p>");

        String donatebutton =
                "<form action='https://www.paypal.com/cgi-bin/webscr' method='post' target='_top'>" +
        "<input type='hidden' name='cmd' value='_donations'>" +
        "<input type='hidden' name='business' value='8U9DK42DS2US2'>" +
        "<input type='hidden' name='lc' value='US'>" +
        "<input type='hidden' name='item_name' value='Grappl'>" +
        "<input type='hidden' name='button_subtype' value='services'>" +
        "<input type='hidden' name='currency_code' value='USD'>" +
        "<input type='hidden' name='bn' value='PP-BuyNowBF:btn_buynow_LG.gif:NonHosted'>" +
        "<table>" +
        "<tr><td><input type='hidden' name='on0' value='Username'>Username</td></tr><tr><td><input type='text' name='os0' maxlength='200'></td></tr>" +
        "</table>" +
        "<input type='image' src='https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif' border='0' name='submit' alt='PayPal - The safer, easier way to pay online!'>" +
        "<img alt='' border='0' src='https://www.paypalobjects.com/en_US/i/scr/pixel.gif' width='1' height='1'>" +
        "</form>";

        response.append(donatebutton);

        httpExchange.sendResponseHeaders(200, response.length());
        httpExchange.getResponseBody().write(response.toString().getBytes());
        httpExchange.getResponseBody().close();
    }
}
