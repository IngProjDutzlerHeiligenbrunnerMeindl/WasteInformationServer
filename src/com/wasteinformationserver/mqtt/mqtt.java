package com.wasteinformationserver.mqtt;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDCB;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
            e.printStackTrace();
        }

        mqttreceiver mr = new mqttreceiver(client);
        mr.addMessageReceivedListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String temp = e.getActionCommand();

                String[] split = temp.split(",");
                getDatabasedata("SELECT pickupdates.pickupdate FROM pickupdates WHERE pickupdates.citywastezoneid=(SELECT cities.zone FROM cities WHERE cities.name='" + split[1] + "' AND cities.wastetype='" + split[2] + "' AND cities.zone=" + split[3] + ")", split[2], Integer.parseInt(split[0]));
            }
        });
        mr.getmessage();
    }

    public void getDatabasedata(String message, String wastetyp, int clientidentify) {

        Log.debug(message);
        JDCB Database = new JDCB("placeuser", "eaL956R6yFItQVBl", "wasteinformation");
        ResultSet result = Database.executeQuery(message);
        try {
            if (!result.isBeforeFirst()) {
                int abholtag = 0;
                transmitmessageAbfallart(clientidentify + "," + wastetyp + "," + abholtag);
            } else {
                while (result.next()) {
                    String temptime = String.valueOf(result.getString("pickupdate"));

                    GregorianCalendar now = new GregorianCalendar();
                    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
                    String date = df.format(now.getTime());
                    String[] parts = temptime.split("-");
                    String tempyear = parts[0];
                    String[] yearsplit = tempyear.split("0");
                    String tempyearnew = yearsplit[1];
                    String newDate = parts[2] + "." + parts[1] + ".20" + tempyearnew;
                    String[] partstwo = date.split(" ");
                    String Datetomorrow=nexDayDate();


                    int abholtag;
                    if (partstwo[0].contains(newDate)||partstwo[0].contains(Datetomorrow)) {
                        abholtag = 1;
                        transmitmessageAbfallart(clientidentify + "," + wastetyp + "," + abholtag);
                    }else {
                        abholtag=0;
                        transmitmessageAbfallart(clientidentify + "," + wastetyp + "," + abholtag);
                    }

                }
            }
        } catch (SQLException e) {
            System.out.println("Exception");
            e.printStackTrace();
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

        String temp=dateFormat.format(currentDatePlusOne);
        String split[]=temp.split("/");
        String newDate=split[2]+"."+split[1]+"."+split[0];
        return newDate;



    }
}