package com.wasteinformationserver.mqtt;

import com.wasteinformationserver.db.jdcb;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.GregorianCalendar;

public class mqtt {

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

        String temptime = null;
        String tempabfallart = null;

        jdcb Database = new jdcb("placeuser", "eaL956R6yFItQVBl", "wasteinformation");
        ResultSet result = Database.executeQuery("SELECT*FROM place WHERE Zone=1");
        try {
            while (result.next()) {
                temptime = String.valueOf(result.getString("Abholtag"));
                tempabfallart = String.valueOf(result.getString("Abfallart"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (temptime != null && tempabfallart != null) {
            transmitmessageAbfallart(tempabfallart);
            //transmitmessageDate(temptime);
        } else {
            System.out.println("NO Connection");
        }
    }


    private void transmitmessageAbfallart(String tempabfallart) {

        mqtttransmitter mt = new mqtttransmitter();
        mt.sendmessage(tempabfallart);
    }

    private void transmitmessageDate(String temptime) {
        GregorianCalendar now = new GregorianCalendar();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG); // 14.04.12 21:34:07 MESZ
        System.out.println(df.format(now.getTime()));

    }
}