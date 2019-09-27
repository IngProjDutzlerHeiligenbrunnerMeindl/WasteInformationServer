package website;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;

import java.net.InetSocketAddress;

public class Webserver {
    public void startserver() {
        System.out.println("starting server");
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.createContext("/", new MainPage());
        server.createContext("/senddata/loginget", new LoginRequest());
        server.createContext("/senddata/registerpost",new RegisterRequest());
        server.createContext("/senddata/checkloginstate",new CheckLoginState());

        server.setExecutor(null); // creates a default executor
        server.start();
    }


}
