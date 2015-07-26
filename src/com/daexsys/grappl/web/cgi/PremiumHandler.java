package com.daexsys.grappl.web.cgi;

import com.daexsys.grappl.server.User;
import com.daexsys.grappl.server.UserManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PremiumHandler implements HttpHandler{
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

        String userName = parts[0].split("\\=")[1];

        User user = UserManager.getUserByName(userName);
        user.setAlphaTester(true);
        user.save();
    }
}
