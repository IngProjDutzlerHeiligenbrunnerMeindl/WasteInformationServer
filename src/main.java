import db.jdcb;

import java.sql.ResultSet;
import java.sql.SQLException;

public class main {
    public static void main(String[] args) {
/*
        Date D=new Date();
        D.getdata();
        D.printList();
        */

        Thread mythread = new Thread(() -> new website.Webserver().startserver());
        mythread.start();


        System.out.println("thread started");



    }
}
