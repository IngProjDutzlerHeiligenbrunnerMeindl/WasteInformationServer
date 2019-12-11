package com.wasteinformationserver.basicutils;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class Info {
    public static String version="0.0";
    public static String builddate;


    public static void init(){
        Properties prop = new Properties();
        try {
            URL url = Info.class.getResource("/version.properties");

            prop.load(url.openStream());
            version=(String)prop.get("version");
            builddate=(String)prop.get("buildtime");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
