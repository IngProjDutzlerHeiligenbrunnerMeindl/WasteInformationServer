package com.wasteinformationserver.mqtt;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDCB;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MqttService {
    private MqttClient client = null;
    private String serveruri;

    public MqttService(String serverurl, String port) {
        serveruri= "tcp://"+serverurl+":"+port;
    }

    public void startupService() {

        try {
            client = new MqttClient(serveruri, "JavaSample42");
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            client.connect(connOpts);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    Log.error("connection lost");
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) {
                    String message = new String(mqttMessage.getPayload());
                    Log.info("received Request from PCB");

                    Log.debug("received message");
                    String[] split = message.split(",");
                    String wastetyp = getTyp(Integer.parseInt(split[2]));
                    // TODO: 12.01.20 check if id is in db -- save when not
                    checkDatabase(wastetyp, Integer.parseInt(split[0]), split[1], Integer.parseInt(split[3]));
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

    public void checkDatabase(String wastetyp, int clientidentify, String cityname, int zone) {
        Log.debug(wastetyp);
        Log.debug(clientidentify);

        JDCB Database = null;
        try {
            Database = JDCB.getInstance();
        } catch (IOException e) {
            Log.error("No Connection to the databank");
        }
        int wastenumber = getIntTyp(wastetyp);

        ResultSet result = Database.executeQuery("SELECT pickupdates.pickupdate FROM pickupdates WHERE pickupdates.citywastezoneid=(SELECT cities.id FROM cities WHERE cities.name='" + cityname + "' AND cities.wastetype='" + wastetyp + "' AND cities.zone=" + zone + ")");
        try {
            result.last();
            if (result.getRow() == 0){
                //if not found in db --> send zero
                Log.debug("not found in db");
                tramsmitMessage(clientidentify + "," + wastenumber + "," + 0);
            }else{
                Log.debug(result.getString("pickupdate"));

                result.first();
                do {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    long timestamp = formatter.parse(result.getString("pickupdate")).getTime();
                    long timestampnow = formatter.parse(formatter.format(new Date())).getTime(); // todo more fancy
                    Log.debug("timestamp is :" + timestamp);

                    if (timestamp == timestampnow || timestamp == timestampnow + 86400000) { // 86400000 == one day
                        // valid time
                        tramsmitMessage(clientidentify + "," + wastenumber + "," + 1);
                        Log.debug("valid time");
                        return;
                    }
                }while(result.next());
                tramsmitMessage(clientidentify + "," + wastenumber + "," + 0); //transmit zero if not returned before
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void tramsmitMessage(String temp)  {
        Log.debug("sending message >>>"+temp);
        MqttMessage message = new MqttMessage(temp.getBytes());
        message.setQos(2);
        try {
            client.publish("TopicOut", message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
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
        switch (temp) {
            case "Plastic":
                number = 1;
                break;
            case "Metal":
                number = 2;
                break;
            case "Residual waste":
                number = 3;
                break;
            case "Biowaste":
                number = 4;
                break;
        }
        return number;
    }
}