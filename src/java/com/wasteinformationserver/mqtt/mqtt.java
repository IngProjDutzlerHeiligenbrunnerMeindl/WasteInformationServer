package com.wasteinformationserver.mqtt;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDCB;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class mqtt {
    MqttClient client = null;

    public mqtt() {

    }

    public void notifymessage() {

        try {
            client = new MqttClient("tcp://192.168.65.15:1883", "JavaSample");
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            client.connect(connOpts);

        } catch (MqttException e) {
            Log.error("Connection to the ESB was failed");
        }

        mqttreceiver mr = new mqttreceiver(client);
        mr.addMessageReceivedListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String temp = e.getActionCommand();

                String[] split = temp.split(",");
                String wastetyp = getTyp(Integer.parseInt(split[2]));
                getDatabasedata("SELECT pickupdates.pickupdate FROM pickupdates WHERE pickupdates.citywastezoneid=(SELECT cities.zone FROM cities WHERE cities.name='" + split[1] + "' AND cities.wastetype='" + wastetyp + "' AND cities.zone=" + split[3] + ")", wastetyp, Integer.parseInt(split[0]));
            }
        });
        mr.getmessage();
    }

    public void getDatabasedata(String message, String wastetyp, int clientidentify) {

        Log.debug(message);
        Log.debug(wastetyp);
        Log.debug(clientidentify);
        JDCB Database = null;
        try {
            Database = JDCB.getInstance();
        } catch (IOException e) {
            Log.error("No Connection to the databank");
        }
        int wastenumber = getIntTyp(wastetyp);
        //new JDCB("placeuser", "eaL956R6yFItQVBl", "wasteinformation");
        ResultSet result = Database.executeQuery(message);
        try {
            while (result.next()) {
                String temptime = String.valueOf(result.getString("pickupdate"));


                String newDate = getDate(temptime);
                GregorianCalendar now = new GregorianCalendar();
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
                String date = df.format(now.getTime());
                String[] partstwo = date.split(",");
                String Datetomorrow = nexDayDate();

                int abholtag;
                if (partstwo[0].equals(newDate) || partstwo[0].equals(Datetomorrow)) {
                    abholtag = 1;
                    transmitmessageAbfallart(clientidentify + "," + wastenumber + "," + abholtag);
                } else {
                    abholtag = 0;
                    transmitmessageAbfallart(clientidentify + "," + wastenumber + "," + abholtag);
                }
            }
        } catch (SQLException e) {
            Log.error("No data from database");
        }
    }


    private void transmitmessageAbfallart(String temp) {

        mqtttransmitter mt = new mqtttransmitter(client);
        Log.debug(temp);
        mt.sendmessage(temp);
    }

    private String nexDayDate() {

        final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date currentDate = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, 1);
        Date currentDatePlusOne = c.getTime();

        String temp = dateFormat.format(currentDatePlusOne);
        String split[] = temp.split("/");
        String newDate = split[2] + "." + split[1] + "." + split[0];
        return newDate;
    }

    private String getTyp(int number) {
        if (number == 1) {
            return "Plastic";
        } else if (number == 2) {
            return "Metal";
        } else if (number == 3) {
            return "Residual waste";
        } else if (number == 4) {
            return "Biowaste";
        }
        return null;
    }

    private int getIntTyp(String temp) {
        int number = 0;
        if (temp.equals("Plastic")) {
            number = 1;
        } else if (temp.equals("Metal")) {
            number = 2;
        } else if (temp.equals("Residual waste")) {
            number = 3;
        } else if (temp.equals("Biowaste")) {
            number = 4;
        }
        return number;
    }

    private String getDate(String temptime) {
        GregorianCalendar now = new GregorianCalendar();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
        String[] parts = temptime.split("-");
        String tempyear = parts[0];
        String[] yearsplit = tempyear.split("0");
        String tempyearnew = yearsplit[1];
        return parts[2] + "." + parts[1] + "." + tempyearnew;
    }
}