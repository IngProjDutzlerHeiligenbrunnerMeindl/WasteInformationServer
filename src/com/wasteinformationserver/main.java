package com.wasteinformationserver;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.website.Webserver;

public class main {
    public static void main(String[] args) {
/*
        com.wasteinformationserver.Date D=new com.wasteinformationserver.Date();
        D.getdata();
        D.printList();
        */

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
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

        Log.message("thread started");



    }
}
