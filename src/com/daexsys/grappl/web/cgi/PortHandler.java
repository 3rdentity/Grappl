package com.daexsys.grappl.web.cgi;

import com.daexsys.grappl.server.User;
import com.daexsys.grappl.server.UserManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;

public class PortHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
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

        String port = parts[0].split("\\=")[1];
        int tport = Integer.parseInt(port);

        boolean set = true;

        if(tport < 0) set = false;
        if(!portExists(tport)) set = false;

        // If the localPort is over 0
        if(set) {
            User user = UserManager.getUser(httpExchange);
            user.setFixedPort(tport);
            set = true;
            user.save();
        }

        String response = "";
        if(set) {
            // Create response
            response = "<script language='javascript'>window.location.href = '/account'</script>";
        } else {
            response = "<script language='javascript'>window.location.href = '/account?port=unavailable'</script>";
        }

        httpExchange.sendResponseHeaders(200, response.length());
        httpExchange.getResponseBody().write(response.getBytes());
        httpExchange.getResponseBody().close();
    }

    public static boolean portExists(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.close();
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
