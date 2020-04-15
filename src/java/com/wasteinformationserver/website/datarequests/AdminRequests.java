package com.wasteinformationserver.website.datarequests;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.website.basicrequest.PostRequest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * all requests for Admin page
 *
 * @author Lukas Heiligenbrunner
 */
public class AdminRequests extends PostRequest {
    @Override
    public String request(HashMap<String, String> params) {
        String result = "";
        switch (params.get("action")) {
            /**
             * shut down the whole application
             */
            case "shutdownserver":
                System.exit(0);
                break;
            /**
             * restart the server application
             */
            case "restartserver":
                final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
                File currentJar = null;
                try {
                    currentJar = new File(AdminRequests.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                /* is it a jar file? */
                if (!currentJar.getName().endsWith(".jar"))
                    Log.Log.warning("not jar --> cant restart");

                /* Build command: java -jar application.jar */
                final ArrayList<String> command = new ArrayList<>();
                command.add(javaBin);
                command.add("-jar");
                command.add(currentJar.getPath());

                final ProcessBuilder builder = new ProcessBuilder(command);
                try {
                    builder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
                break;
        }

        return result;
    }
}
