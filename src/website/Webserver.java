package website;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Webserver{
    public void startserver(){
        System.out.println("starting server");
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.createContext("/", new MainPage());

        server.createContext("/senddata/loginget", httpExchange -> {
            if (httpExchange.getRequestMethod().equals("GET")){
                String query = httpExchange.getRequestURI().getQuery();
                System.out.println(query);

                HashMap<String, String> params = new HashMap<>();

                String[] res = query.split("&");
                for (String str : res){
                    String[] values = str.split("=");
                    params.put(values[0],values[1]);

                }
                String password = params.get("password");
                String username = params.get("username");

                System.out.println(StringToMD5(password));
                //TODO check if user exists in database


                //send response
                String response = "{\"accept\": true}";

                Headers h = httpExchange.getResponseHeaders();
                h.set("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(200, 0);

                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public String StringToMD5(String value){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(value.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            return no.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    static class MainPage implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            System.out.println("a new request...");
            String root = "./wwwroot";
            URI uri = t.getRequestURI();
            String path;

            if (uri.getPath().equals("/")){
                path = "/index.html";
            }else{
                path = uri.getPath();
            }
            System.out.println("looking for: "+ root + path);

            File file = new File(root + path).getCanonicalFile();

            if (!file.isFile()) {
                // Object does not exist or is not a file: reject with 404 error.
                String response = "404 (Not Found)\n";
                t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                // Object exists and is a file: accept with response code 200.
                String mime = "text/html";
                if(path.substring(path.length()-3).equals(".js")) mime = "application/javascript";
                if(path.substring(path.length()-3).equals("css")) mime = "text/css";

                Headers h = t.getResponseHeaders();
                h.set("Content-Type", mime);
                t.sendResponseHeaders(200, 0);

                OutputStream os = t.getResponseBody();
                FileInputStream fs = new FileInputStream(file);
                final byte[] buffer = new byte[0x10000];
                int count = 0;
                while ((count = fs.read(buffer)) >= 0) {
                    os.write(buffer,0,count);
                }
                fs.close();
                os.close();
            }
        }
    }
}
