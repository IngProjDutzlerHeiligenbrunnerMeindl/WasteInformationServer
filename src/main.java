import db.jdcb;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class main {
    public static void main(String[] args) {
/*
        Date D=new Date();
        D.getdata();
        D.printList();
        */

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Thread.sleep(200);
                System.out.println("Shutting down ...");

                //shutdown routine
            }  catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        Thread mythread = new Thread(() -> new website.Webserver().startserver());
        mythread.start();


        System.out.println("thread started");



    }
}
