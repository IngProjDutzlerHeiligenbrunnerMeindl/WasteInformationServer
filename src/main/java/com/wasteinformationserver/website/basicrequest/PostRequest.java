package com.wasteinformationserver.website.basicrequest;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public abstract class PostRequest implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equals("POST")) {
            StringBuilder sb = new StringBuilder();
            InputStream ios = httpExchange.getRequestBody();
            int i;
            while ((i = ios.read()) != -1) {
                sb.append((char) i);
            }
            String query =  sb.toString();

            HashMap<String, String> params = new HashMap<>();

            String[] res = query.split("&");
            for (String str : res) {
                String[] values = str.split("=");
                params.put(values[0], values[1]);
            }

            String response = request(params);


            Headers h = httpExchange.getResponseHeaders();
            h.set("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(200, 0);

            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public abstract String request(HashMap<String, String> params);

}
