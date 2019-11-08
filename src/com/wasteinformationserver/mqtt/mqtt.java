package com.wasteinformationserver.mqtt;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDCB;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.GregorianCalendar;

public class mqtt {


    public mqtt() {

    }

    public void notifymessage() {

        mqttreceiver mr = new mqttreceiver();
        mr.addMessageReceivedListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String temp = e.getActionCommand();
                System.out.println(temp);

                String[] split = temp.split(",");
                getDatabasedata("SELECT pickupdates.pickupdate FROM pickupdates WHERE pickupdates.citywastezoneid=(SELECT cities.zone FROM cities WHERE cities.name='" + split[1] + "' AND cities.wastetype='" + split[2] + "' AND cities.zone=" + split[3] + ")",split[2], Integer.parseInt(split[0]));
            }
        });
        mr.getmessage();
    }

    public void getDatabasedata(String message,String wastetyp, int clientidentify) {

        String temp;

        Log.debug(message);
        JDCB Database = new JDCB("placeuser", "eaL956R6yFItQVBl", "wasteinformation");
        ResultSet result = Database.executeQuery(message);
        try {
            while (result.next()) {
                String temptime = String.valueOf(result.getString("pickupdate"));

                GregorianCalendar now = new GregorianCalendar();
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
                String date = df.format(now.getTime());

                String[] parts = temptime.split("-");
                String tempyear = parts[0];

                String[] yearsplit = tempyear.split("0");
                String tempyearnew = yearsplit[1];

                String newDate = parts[2] + "." + parts[1] + "." + tempyearnew;

                String[] partstwo = date.split(" ");


                int abholtag;
                if (partstwo[0].contains(newDate)) {
                    abholtag = 1;
                } else {
                    abholtag = 0;
                }
                temp = clientidentify+","+ wastetyp + "," + abholtag;
                System.out.println(temp);


                if (temp != null) {
                    transmitmessageAbfallart(temp);
                } else {
                    Log.debug("NO Connection");
                }
            }
        } catch (SQLException e) {
            System.out.println("Exception");
            e.printStackTrace();
        }

    }



    private void transmitmessageAbfallart(String temp) {

        mqtttransmitter mt = new mqtttransmitter();
        Log.debug(temp);
        mt.sendmessage(temp);
    }
}