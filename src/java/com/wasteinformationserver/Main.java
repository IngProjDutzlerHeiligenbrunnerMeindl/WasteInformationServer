package com.wasteinformationserver;

import com.wasteinformationserver.basicutils.Info;
import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDBC;
import com.wasteinformationserver.mqtt.MqttService;
import com.wasteinformationserver.website.Webserver;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Log.setLevel(Log.DEBUG);
        Info.init();

        Log.info("startup of WasteInformationServer");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Thread.sleep(200);
                Log.warning("Shutting down ...");

                //shutdown routine
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        Log.info("Server version: " + Info.getVersion());
        Log.debug("Build date: " + Info.getBuilddate());

        //initial connect to db
        Log.message("initial login to db");
        try {
            JDBC.init("ingproject", "Kb9Dxklumt76ieq6", "ingproject", "db.power4future.at", 3306);
            //JDBC.init("users", "kOpaIJUjkgb9ur6S", "wasteinformation", "192.168.65.15", 3306);
        } catch (IOException e) {
            //e.printStackTrace();
            Log.error("no connection to db");
        }


        //startup web server
        Thread mythread = new Thread(() -> new Webserver().startserver());
        mythread.start();


        //startup mqtt service
        Log.message("starting mqtt service");

        MqttService m = new MqttService("mqtt.heili.eu", "1883");
        m.startupService();

    }
}