package website;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public abstract class GetRequest implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equals("GET")) {
            String query = httpExchange.getRequestURI().getQuery();

            HashMap<String, String> params = new HashMap<>();

            String[] res = query.split("&");
            for (String str : res) {
                String[] values = str.split("=");
                params.put(values[0], values[1]);

            }

            String response = myrequest(params);


            Headers h = httpExchange.getResponseHeaders();
            h.set("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(200, 0);

            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    /**
     *
     * @param params received get params from website
     * @return json reply to website
     */
    public abstract String myrequest(HashMap<String, String> params);

}
