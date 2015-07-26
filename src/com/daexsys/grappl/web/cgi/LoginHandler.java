package com.daexsys.grappl.web.cgi;

import com.daexsys.grappl.server.User;
import com.daexsys.grappl.server.UserManager;
import com.daexsys.grappl.web.WebServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LoginHandler implements HttpHandler {
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

            if (UserManager.personExists(name.toLowerCase())) {
                String pass = parts[1].split("\\=")[1];
                    User user = UserManager.getUserByName(name);
                    String password = user.getPassword();

                    String response = "";

                    // If the passwords match
                    if (password.equalsIgnoreCase("" + pass.hashCode())) {
                        // Create login instance id
                        String hashID = name.hashCode() + System.currentTimeMillis() + "";

                        // Login user
                        UserManager.loginUser(user, hashID);

                        // Create response
                        response = "<script language='Javascript'>document.cookie='id=" + hashID + "';domain='grappl.io';";


                        response += "window.location.href = '/account'</script>";
                    } else {
                        response = "Incorrect login details.";
                    }

                    httpExchange.sendResponseHeaders(200, response.length());
                    httpExchange.getResponseBody().write(response.getBytes());
                    httpExchange.getResponseBody().close();
                }
             else {
                String derp =
                       "<html>"+WebServer.getTop() +
                                "Login Failure" +
                               "Account does not exist! " +
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
