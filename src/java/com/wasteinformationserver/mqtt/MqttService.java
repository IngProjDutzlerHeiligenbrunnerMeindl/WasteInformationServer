package com.wasteinformationserver.mqtt;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDCB;
import org.eclipse.paho.client.mqttv3.*;

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

public class MqttService {
    MqttClient client = null;

    public MqttService() {

    }

    public void startupService() {

        try {
            client = new MqttClient("tcp://192.168.65.15:1883", "JavaSample42");
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            client.connect(connOpts);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    Log.error("connection lost");
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    String message = new String(mqttMessage.getPayload());
                    Log.info("received Request from PCB");

                    Log.debug("received message");
                    String[] split = message.split(",");
                    String wastetyp = getTyp(Integer.parseInt(split[2]));
                    getDatabasedata("SELECT pickupdates.pickupdate FROM pickupdates WHERE pickupdates.citywastezoneid=(SELECT cities.zone FROM cities WHERE cities.name='" + split[1] + "' AND cities.wastetype='" + wastetyp + "' AND cities.zone=" + split[3] + ")", wastetyp, Integer.parseInt(split[0]));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            client.subscribe("TopicIn");
        } catch (MqttException e) {
            Log.error("Connection to the Broker failed");
        }
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

        ResultSet result = Database.executeQuery(message);
        try {
            result.last();
            if (result.getFetchSize() == 0){
                //if not found in db --> send zero
                transmitmessageAbfallart(clientidentify + "," + wastenumber + "," + 0);
            }
            result.first();
            while (result.next()) {
                String newDate = getDateDatabase(String.valueOf(result.getString("pickupdate")));
                String currentDate = getcurrentDate();
                String Datetomorrow = nexDayDate();

                if (currentDate.equals(newDate) || currentDate.equals(Datetomorrow)) {
                    transmitmessageAbfallart(clientidentify + "," + wastenumber + "," + 1);
                } else {
                    transmitmessageAbfallart(clientidentify + "," + wastenumber + "," + 0);
                }
            }
        } catch (SQLException e) {
            Log.error("No data from database");
        }
    }


    private void transmitmessageAbfallart(String temp)  {
        Log.debug("sending message >>>"+temp);
        MqttMessage message = new MqttMessage(temp.getBytes());
        message.setQos(2);
        try {
            client.publish("TopicOut", message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private String nexDayDate() {
        // TODO: 10.01.20 doesnt work 
        final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date currentDate = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, 1);
        Date currentDatePlusOne = c.getTime();

        String temp = dateFormat.format(currentDatePlusOne);
        String split[] = temp.split("/");
        return split[2] + "." + split[1] + "." + split[0];
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

    private String getDateDatabase(String temptime) {
        String[] parts = temptime.split("-");
        String tempyear = parts[0];
        String[] yearsplit = tempyear.split("0");
        String tempyearnew = yearsplit[1];
        return parts[2] + "." + parts[1] + "." + tempyearnew;
    }

    private String getcurrentDate() {
        GregorianCalendar now = new GregorianCalendar();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
        String date = df.format(now.getTime());
        String[] partstwo = date.split(",");
        return partstwo[0];
    }
}