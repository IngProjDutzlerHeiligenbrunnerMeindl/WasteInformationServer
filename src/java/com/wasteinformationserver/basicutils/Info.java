package com.wasteinformationserver.basicutils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Info {
    public static String version="0.0";
    public static String builddate;


    public static void init(){
        Properties prop = new Properties();
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

            URL url = Info.class.getResource("/version.properties");

            String builddatee = format.format(new Date(new File(url.toURI()).lastModified()));
            builddate=builddatee;

            prop.load(url.openStream());
            version=(String)prop.get("version");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
}
