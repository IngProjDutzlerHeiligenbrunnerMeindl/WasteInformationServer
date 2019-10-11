package com.wasteinformationserver.mqtt;

import com.wasteinformationserver.db.jdcb;

import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class mqtt {

    private ArrayList<String> mylist = new ArrayList<>();
    private int index = 0;

    public mqtt() {

    }

    public void notifymessage() {

        mqttreceiver mr = new mqttreceiver();

        mr.addMessageReceivedListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getActionCommand());
            }
        });
    }

    public void getDatabasedata() {
        int n=0;

        jdcb Database = new jdcb("placeuser", "eaL956R6yFItQVBl", "wasteinformation");
        ResultSet result = Database.executeQuery("SELECT*FROM place WHERE Zone=1");
        try {
            while (result.next()) {
                String temp = String.valueOf(result.getString("Abholtag"));
                System.out.println(temp);
                filllist(temp);
                n++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        transmitmessagetoESP();
    }


    private void transmitmessagetoESP() {
        mqtttransmitter mt = new mqtttransmitter(mylist);
    }

    private void filllist(String temp) {
        mylist.add(index, temp);
    }

    public void printlist() {
        for (int n = 0; n < index; n++) {
            System.out.println(mylist.get(index));
        }
    }

}
