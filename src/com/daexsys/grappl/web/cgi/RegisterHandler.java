package com.daexsys.grappl.web.cgi;

import com.daexsys.grappl.server.User;
import com.daexsys.grappl.server.UserManager;
import com.daexsys.grappl.web.WebServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RegisterHandler implements HttpHandler {
    public static Map<String, Integer> ipToAccounts = new HashMap<String, Integer>();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
        InputStream inputStream = httpExchange.getRequestBody();
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        String out = "";

        boolean loop = true;

        while(loop) {
            String buffer = dataInputStream.readLine();
            if(buffer == null) {
                loop = false;
            } else {
                out = buffer;
            }
        }

        String uri = "?" + out;

        String[] spl = uri.split("\\?");
        String[] parts = spl[1].split("\\&");

        String name = parts[0].split("\\=")[1].toLowerCase();

            try {
                ipToAccounts.put(httpExchange.getRemoteAddress().getAddress().toString(), ipToAccounts.get(httpExchange.getRemoteAddress().getAddress().toString()) + 1);

            } catch (Exception e) {

                ipToAccounts.put(httpExchange.getRemoteAddress().getAddress().toString(), 1);
            }

        if (!UserManager.personExists(name.toLowerCase())) {
            String pass = parts[1].split("\\=")[1];
            String pass2 = parts[2].split("\\=")[1];

            if(pass.equalsIgnoreCase(pass2)) {
                if(ipToAccounts.get(httpExchange.getRemoteAddress().getAddress().toString()) < 10) {
                    User user = new User(name, pass.hashCode() + "", -1);
                    user.save();
                    UserManager.addUser(user.getUsername(), user);
                } else if(ipToAccounts.get(httpExchange.getRemoteAddress().getAddress().toString()) % 10 == 0) {
                    System.out.println("ALERT: " + httpExchange.getRemoteAddress().getAddress() + " has attempted to create " + ipToAccounts.get(httpExchange.getRemoteAddress().getAddress().toString()) + " accounts!");
                }

                String response = WebServer.getTailoredTop(httpExchange) + "<body bgcolor = '#DBEDF8'>" + WebServer.getTop() + "Registration complete! <a href = '/login'>Login</a>";

                httpExchange.sendResponseHeaders(200, response.length());
                httpExchange.getResponseBody().write(response.getBytes());
                httpExchange.getResponseBody().close();
            }
            else {
                String response = WebServer.getTop() + "Passwords do not match! <a href = '/register'>back</a>";
                httpExchange.sendResponseHeaders(200, response.length());
                httpExchange.getResponseBody().write(response.getBytes());
                httpExchange.getResponseBody().close();
            }
        } else {
            String derp =
                    "<html>" +WebServer.getTop() +
                            "Register Failure" +
                            " - Account already exists! " +
                            "<a href = '/'>Back to home</a></center>";


            httpExchange.sendResponseHeaders(200, derp.length());
            httpExchange.getResponseBody().write(derp.getBytes());
            httpExchange.getResponseBody().close();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    }
}
