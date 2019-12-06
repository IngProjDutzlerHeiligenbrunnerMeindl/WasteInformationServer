package com.wasteinformationserver;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDCB;
import com.wasteinformationserver.mqtt.*;
import com.wasteinformationserver.website.Webserver;

import java.io.IOException;

public class main {
    public static void main(String[] args) {

        Log.setLevel(Log.DEBUG);


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Thread.sleep(200);
                Log.warning("Shutting down ...");

                //shutdown routine
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        //initial connect to db
        Log.message("initial login to db");
        new Thread(() -> {
            try {
                JDCB.init("users", "kOpaIJUjkgb9ur6S", "wasteinformation", "192.168.65.15", 3306);
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
            e.printStackTrace();
        }


    }
}
