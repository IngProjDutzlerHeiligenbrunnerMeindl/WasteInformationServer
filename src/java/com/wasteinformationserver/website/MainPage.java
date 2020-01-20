// Dear programmer:
// When I wrote this code, only god and
// I knew how it worked.
// Now, only god knows it!
//
// Therefore, if you are trying to optimize
// this routine and it fails (most surely),
// please increase this counter as a
// warning for the next person:
//
// total hours wasted here = 254
//

package com.wasteinformationserver.website;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.website.datarequests.login.LoginState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainPage implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
        String path = t.getRequestURI().getPath();

        if (path.equals("/")) {
            path += "index.html";
        }

        Log.debug("looking for: " + path);

        if (path.contains(".html")) {
            if (LoginState.getObject().isLoggedIn() || path.equals("/register.html") || path.equals("/index.html")) { //pass only register page
                sendPage(path, t);
            } else {
                Log.warning("user not logged in --> redirecting to login page");
                sendPage("/index.html", t);
            }
        } else { //only detect login state on html pages
            sendPage(path, t);
        }
    }

    private void sendPage(String path, HttpExchange t) throws IOException {
        InputStream fs = getClass().getResourceAsStream("/wwwroot" + path);

        if (fs == null && path.contains(".html")) {
            Log.warning("wrong page sending 404");
            sendPage("/404Error.html", t);
        } else if (fs == null) {
            Log.warning("requested resource doesnt exist --> "+path);
        } else {
            // Object exists and is a file: accept with response code 200.
            String mime = "text/html";
            final String s = path.substring(path.length() - 3);
            if (s.equals(".js")) mime = "application/javascript";
            if (s.equals("css")) mime = "text/css";

            Headers h = t.getResponseHeaders();
            h.set("Content-Type", mime);
            t.sendResponseHeaders(200, 0);

            OutputStream os = t.getResponseBody();

            final byte[] buffer = new byte[0x10000];
            int count;
            while ((count = fs.read(buffer)) >= 0) {
                os.write(buffer, 0, count);
            }
            fs.close();
            os.close();
        }
    }
}