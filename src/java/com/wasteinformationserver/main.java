package com.wasteinformationserver;

import com.wasteinformationserver.basicutils.Info;
import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDCB;
import com.wasteinformationserver.mqtt.mqtt;
import com.wasteinformationserver.website.Webserver;
import java.io.IOException;

public class main {
    public static void main(String[] args) {

        Log.setLevel(Log.DEBUG);
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

        Info.init();
        Log.info("Server version: "+Info.getVersion());
        Log.debug("Build date: "+Info.getBuilddate());

        //initial connect to db
        Log.message("initial login to db");
        new Thread(() -> {
            try {
                JDCB.init("ingproject", "Kb9Dxklumt76ieq6", "ingproject", "db.power4future.at", 3306);
                //JDCB.init("users", "kOpaIJUjkgb9ur6S", "wasteinformation", "192.168.65.15", 3306);
            } catch (IOException e) {
                //e.printStackTrace();
                Log.error("no connection to db");
            }
        }).start();


        //startup web server
        Thread mythread = new Thread(() -> new Webserver().startserver());
        mythread.start();


        //startup mqtt service
        Log.message("starting mqtt service");
        try {
            mqtt m = new mqtt();
            m.notifymessage();
        } catch (Exception e) {
            Log.debug("An error was happened in the class mqtt");
        }
    }
}
