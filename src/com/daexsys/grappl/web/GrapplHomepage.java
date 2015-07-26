package com.daexsys.grappl.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class GrapplHomepage implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Homepage request received " + httpExchange.getRemoteAddress().toString());

        try {
            StringBuilder response = new StringBuilder();

            response.append("<html>");
            response.append("<head>");
            response.append("<link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>");

            response.append("<link rel = 'icon' href = 'http://grappl.io:888/html/grapplicon.ico' type='image/x-icon'>");
            response.append("<title>grappl - you are the cloud now</title>");
            response.append("</head>");

            response.append(WebServer.getTailoredTop(httpExchange));

            response.append("<div align='center' style='background-color:#6e95d6;  border-radius: 10px 10px 10px 10px;a{color:#ffffff}'>");

            response.append("<div align = 'center' style='background-color:#4676c9;  border-radius: 10px 10px 10px 10px;a{color:#ffffff}'>");

            response.append("<font size = '6' face = 'Open Sans' color = 'FFFFFF'>");
            response.append("grappl<br></font>");
            response.append("<font size = '5' face = 'Open Sans'  color = 'E5E9FF'>");
            response.append("<div style = 'p{font-family: 'Open Sans', sans-serif;}'>");
            response.append("you are the cloud now<p>");
            response.append("</div>");
            response.append("</div>");

            response.append("<p>");

            response.append("<font size = '3' face = 'Open Sans' color='#ffffff'>");
            response.append("<b>grappl</b> is a clever little tool that lets you host any type of server anywhere for free, with no port forwarding and no VPS.<br>");
            response.append("Your router doesn't even have to support uPnP. You don't even have to have a router. You can do it through your phone!<p>");

            response.append("<img src = 'http://grappl.io:888/html/grapplshot.png'><p>");

            response.append("<b>How to Run</b><br>");
            response.append("Start it.<br>");
            response.append("Either log in or choose to be anonymous.<br>");
            response.append("Enter your server's local port number.<br>");
            response.append("The public address will appear in the grappl window.");
            response.append("<p>");
            response.append("</font>");

            response.append("<p>");
            response.append("<div style='background-color:#4676c9; border-radius: 10px 10px 10px 10px;'>");
            response.append("<b>Download grappl and get hosting:</b>");
            response.append(" <a style='color:#ffffff' href = 'http://grappl.io:888/html/GrapplLauncher.exe' style='a:visited{color:white;}'>Windows</a>");
            response.append(" - <a style='color:#ffffff' href = 'http://grappl.io:888/html/GrapplLauncher.jar' style='a:visited{color:white;}'>OSX & Linux</a>");
            response.append("<p>");

            response.append("<a style='color:#ffffff' href = '/donate' style='a:visited{color:white;}'>DONATE to help keep grappl online and free!</a><p>");

            response.append("<a style='color:#ffffff' href = '/servers'><b><font color = lime>Servers looking for players [new]</b></a></font>" +
                    "<p>");
            response.append("<a style='color:#ffffff' href = '/status'>server statuses</a> --- ");
            response.append("<a style='color:#ffffff' href = 'https://github.com/Cactose/Grappl'>the source</a> --- ");
            response.append("<a style='color:#ffffff'href = '/faq'>faq</a> --- ");
            response.append("<a style='color:#ffffff'href = '/roadmap'>development roadmap</a><p>");

            response.append("</div>");
            response.append("</div>");

            response.append("<div align ='left'>");
            response.append("<a style='color:blue' href = '/terms'>terms</a> - <a style='color:blue' href = 'mailto:matt@daexsys.com'>report abuse</a>");
            response.append("</div>");

            response.append("<div style='width:800px;'>");
            response.append("<font color = '#333333'><b>developer twitter</b></font><p>");
            response.append("<a class=\"twitter-timeline\" href=\"https://twitter.com/Cactose\" " +
                    "data-widget-id=\"616471055713083392\">Tweets by @Cactose</a>\n" +
                    "<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)" +
                    "?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+\"://platform" +
                    ".twitter.com/widgets.js\";fjs.parentNode.insertBefore(js,fjs);}}(document,\"script\"," +
                    "\"twitter-wjs\");</script>");

            httpExchange.sendResponseHeaders(200, response.length());
            httpExchange.getResponseBody().write(response.toString().getBytes());
            httpExchange.getResponseBody().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
