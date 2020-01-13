package com.wasteinformationserver.website;

import com.sun.net.httpserver.HttpServer;
import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.website.datarequests.*;
import com.wasteinformationserver.website.datarequests.login.CheckLoginState;
import com.wasteinformationserver.website.datarequests.login.LoginRequest;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;


public class Webserver {
    public void startserver() {
        Log.info("starting Webserver");

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

            server.createContext("/", new MainPage());

            server.createContext("/senddata/loginget", new LoginRequest());
            server.createContext("/senddata/registerpost", new RegisterRequest());
            server.createContext("/senddata/checkloginstate", new CheckLoginState());
            server.createContext("/senddata/wastedata", new DataRequest());
            server.createContext("/senddata/admindata", new AdminRequests());
            server.createContext("/senddata/newdate", new NewDateRequest());
            server.createContext("/senddata/Devicedata", new DeviceRequest());

            server.setExecutor(null); // creates a default executor
            server.start();
            Log.info("Server available at http://127.0.0.1:8000 now");
        } catch (BindException e) {
            Log.criticalerror("The Port 8000 is already in use!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
