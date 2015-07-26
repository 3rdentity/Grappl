package com.daexsys.grappl.web;

import com.daexsys.grappl.GrapplServerState;
import com.daexsys.grappl.server.User;
import com.daexsys.grappl.server.UserManager;
import com.daexsys.grappl.web.account.Account;
import com.daexsys.grappl.web.account.Login;
import com.daexsys.grappl.web.account.Logout;
import com.daexsys.grappl.web.account.Register;
import com.daexsys.grappl.web.cgi.*;
import com.daexsys.grappl.web.list.ServerList;
import com.daexsys.grappl.web.list.ServerStatus;
import com.sun.net.httpserver.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;

/**
 * Used to handle the web frontend for Grappl.
 */
public class WebServer {

    public static HttpServer httpsServer;

    public static void main(String[] args) {
        try {
            httpsServer = HttpServer.create(new InetSocketAddress((GrapplServerState.testingMode ? 801 : 80)), 0);

            // start https stuff
//            SSLContext sslContext = null;
//
//            try {
//                sslContext = SSLContext.getInstance("TLS");
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            }
//
//            sslContext.init();
////
////            char[] password = "drone".toCharArray();
////
//            httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
//
//                public void configure(HttpsParameters params) {
//                    SSLContext context = getSSLContext();
//                    SSLParameters parameters = context.getDefaultSSLParameters();
//                    parameters.setNeedClientAuth(true);
//                    params.setSSLParameters(parameters);
//                }
//
//            });
            // end https stuff

            httpsServer.createContext("/", new GrapplHomepage());
            httpsServer.createContext("/donate", new GrapplDonate());
            httpsServer.createContext("/stats", new GrapplStats());
            httpsServer.createContext("/register", new Register());
            httpsServer.createContext("/login", new Login());
            httpsServer.createContext("/account", new Account());
            httpsServer.createContext("/registerhandler", new RegisterHandler());
            httpsServer.createContext("/logout", new Logout());
            httpsServer.createContext("/auth", new LoginHandler());
            httpsServer.createContext("/terms", new Terms());
            httpsServer.createContext("/setport", new PortHandler());
            httpsServer.createContext("/secret", new PremiumUp());
            httpsServer.createContext("/p2", new PremiumHandler());
            httpsServer.createContext("/faq", new FAQ());
            httpsServer.createContext("/status", new ServerStatus());
            httpsServer.createContext("/privacy", new PrivacyPolicy());
            httpsServer.createContext("/servers", new ServerList());
            httpsServer.createContext("/roadmap", new Roadmap());

            httpsServer.setExecutor(Executors.newCachedThreadPool());
            httpsServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getTop() {
        StringBuilder response = new StringBuilder("");

        response.append("<center>");
        response.append("<div align='center' style='background-color:#6e95d6; width:1200px;'>");
        response.append("<div align = 'center' style='background-color:#4676c9;'");
        response.append("<font color = 'E5E9FF' face = 'Open Sans' >");
        response.append("<font size = '6' face = 'Open Sans'color = 'FFFFFF'>");
        response.append("grappl<br></font>");
        response.append("<font size = '5' face = 'Open Sans' color = 'E5E9FF'>");
        response.append("you are the cloud now<p>");
        response.append("</div>");

        response.append("<p>");
        return response.toString();
    }

    public static String getTailoredTop(HttpExchange httpExchange) {
        try {
            User user = UserManager.getUser(httpExchange);

            StringBuilder response = new StringBuilder("");
            Color color = new Color(0xDBEDF8);
//            response.append("<head>");
            response.append("<html><link rel = 'icon' href = 'http://grappl.io:888/html/grapplicon.ico' type='image/x-icon'>");
//            response.append("</head>");
            response.append("<title>grappl - you are the cloud now</title>");
            response.append("<body bgcolor = '#DBEDF8'>");
            response.append("<center>");
            response.append("<div style = 'width:1200px;'>");
            response.append("<div style='background-color:#666666; color:#ffffff; font-size: 120%}'>");

            if (user.getUsername().equalsIgnoreCase("ERROR")) {
                response.append("");
                response.append("<div style='float:left'><a href = '/' style='text-align:left'><img src = 'http://grappl.io:888/html/logo.png'></a></div>");

                response.append("<div style='float:right'><b><a href = '/register' style='color:#ffffff;text-decoration:none'>create account</a> " + "- ");
                response.append("<a href = '/login' style='color:#ffffff;text-decoration:none'>login</a></b></div>");

                response.append("<div style = 'clear:both;'></div>");


                response.append("</div>");
                response.append("<p>");
                return response.toString();
            } else {
                response.append("<body link = 'white'><div style='float:left'><a href = '/'><img src = 'http://grappl.io:888/html/logo.png' style='align:left'></a></div>");
                response.append("<div style='float:right'>hello <b>" + user.getUsername() + "</b> - ");
                response.append("<a href = '/' style='color:#ffffff'>home</a> - ");
                response.append("<a href = '/account' style='color:#ffffff'>your account</a>");
                if (!user.isAlphaTester()) response.append(" - <a href = '/donate' style='color:#ffffff'>donate</a>");
                if (user.getUsername().equalsIgnoreCase("cactose")) {
                    response.append(" - <a href = '/stats' style='color:#ffffff'>stats</a>");
                    response.append(" - <a href = '/secret' style='color:#ffffff'>apply</a>");
                }
                response.append(" - <a href = '/logout' style='color:#ffffff'>logout</a></div>");
                response.append("<div style = 'clear:both;'></div>");
                response.append("<center>");
                response.append("</div>");

                response.append("<p>");
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String header(String text) {
        return "<div style = 'background:#414141; width:100%; border-radius: 10px 10px 10px 10px;'><font color = 'ffffff' size = 7>"+text+"</font></div>";
    }
}
