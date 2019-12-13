package com.wasteinformationserver.basicutils;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Info {
    private static String version="not init";
    private static String builddate="not init";
    private static String starttime="not init";

    public static String getVersion() {
        return version;
    }

    public static String getBuilddate() {
        return builddate;
    }

    public static String getStarttime() {
        return starttime;
    }

    public static void init(){
        starttime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
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

    public static void getMemoryUsage(){
        Runtime runtime = Runtime.getRuntime();

        NumberFormat format = NumberFormat.getInstance();

        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append("free memory: " + format.format(freeMemory / 1024) + "\n");
        sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "\n");
        sb.append("max memory: " + format.format(maxMemory / 1024) + "\n");
        sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "\n");

        System.out.println(sb.toString());
    }
}