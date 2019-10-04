package com.wasteinformationserver;

import com.wasteinformationserver.mqtt.*;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.URISyntaxException;
import java.sql.SQLException;

public class main {
    public static void main(String[] args) {
/*
        com.wasteinformationserver.Date D=new com.wasteinformationserver.Date();
        D.getdata();
        D.printList();
        */

       /* Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Thread.sleep(200);
                Log.warning("Shutting down ...");

                //shutdown routine
            }  catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        Thread mythread = new Thread(() -> new Webserver().startserver());
        mythread.start();

        Log.message("thread started");*/

       mqtt m=new mqtt();
     //  m.notifymessage();
        try {
            m.getDatabasedata();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
