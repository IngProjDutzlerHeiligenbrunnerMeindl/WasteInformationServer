package com.wasteinformationserver;

import com.wasteinformationserver.mqtt.*;
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

        mqtt m = new mqtt();
        //  m.notifymessage();
        m.getDatabasedata();
        m.printlist();
        // Log.message("mqtt irgentwos");

    }
}
