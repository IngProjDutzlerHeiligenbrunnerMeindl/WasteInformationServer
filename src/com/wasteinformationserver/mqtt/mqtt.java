package com.wasteinformationserver.mqtt;

import com.wasteinformationserver.db.jdcb;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class mqtt {

    ArrayList<String> mylist = new ArrayList<>();
    int index = 0;

    public mqtt() {

    }

    public void notifymessage() {


        GregorianCalendar now = new GregorianCalendar();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
        String date = df.format(now.getTime());

        System.out.println(date);

        String[] parts = date.split(" ");
        String temp2=parts[0];
        System.out.println(temp2);
        String[] partstwo=temp2.split("\\.");
        String newDate="20"+partstwo[2]+"-"+partstwo[1]+"-"+partstwo[0];



        mqttreceiver mr = new mqttreceiver();

//        System.out.println(message);

        mr.addMessageReceivedListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String temp = e.getActionCommand();

                String[] split = temp.split(",");
                String message = "SELECT*FROM place WHERE Ort='" + split[0] + "' AND Abfallart='" + split[1] + "' AND Zone='" + split[2] + "' AND ABholtag='"+newDate+"'";

                getDatabasedata(message);
            }
        });

        mr.getmessage();

    }

    public void getDatabasedata(String message) {

        String temp;


        System.out.println(message);
        jdcb Database = new jdcb("placeuser", "eaL956R6yFItQVBl", "wasteinformation");
        ResultSet result = Database.executeQuery(message);
        try {
            while (result.next()) {
                String temptime = String.valueOf(result.getString("Abholtag"));
                String tempabfallart = String.valueOf(result.getString("Abfallart"));

                GregorianCalendar now = new GregorianCalendar();
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
                String date = df.format(now.getTime());

                String[] parts = temptime.split("-");
                String tempyear = parts[0];

                String[] yearsplit = tempyear.split("0");
                String tempyearnew = yearsplit[1];

                String newDate = parts[2] + "." + parts[1] + "." + tempyearnew;

                String[] partstwo = date.split(" ");


                boolean abholtag;
                if (partstwo[0].contains(newDate)) {
                    abholtag = true;
                } else {
                    abholtag = false;
                }
                temp = tempabfallart + "," + abholtag;


                if (temp != null) {
                    transmitmessageAbfallart(temp);
                } else {
                    System.out.println("NO Connection");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    private void transmitmessageAbfallart(String temp) {

        mqtttransmitter mt = new mqtttransmitter();
        System.out.println(temp);
        mt.sendmessage(temp);
    }
}