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
    JDCB db;

    public MqttService(String serverurl, String port) {
        serveruri = "tcp://" + serverurl + ":" + port;
        try {
            db = JDCB.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    // TODO: 12.01.20 reconnect
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) {
                    String message = new String(mqttMessage.getPayload());
                    Log.message("received Request from PCB");

                    ResultSet res = db.executeQuery("SELECT * from devices WHERE DeviceID=" + message);
                    try {
                        res.last();
                        if (res.getRow() != 0) {
                            //existing device
                            res.first();
                            int cityid = res.getInt("CityID");
                            if (cityid == -1) {
                                //device not configured yet
                                tramsmitMessage(message + ",-1");
                            } else {
                                checkDatabase(Integer.parseInt(message));
                            }
                        } else {
                            //new device
                            db.executeUpdate("INSERT INTO devices (DeviceID) VALUES (" + message + ")");
                            Log.info("new device registered to server");
                            tramsmitMessage(message + ",-1");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
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

    public void checkDatabase(int deviceid) {
        ResultSet result = db.executeQuery("SELECT pickupdates.pickupdate FROM pickupdates WHERE pickupdates.citywastezoneid=" + deviceid);
        try {
            result.last();
            if (result.getRow() == 0) {
                //if not found in db --> send zero
                Log.debug("not found in db");
                tramsmitMessage(deviceid + "," + "Plastic" + "," + 0);
            } else {
                Log.debug(result.getString("pickupdate"));

                result.first();
                do {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    long timestamp = formatter.parse(result.getString("pickupdate")).getTime();
                    long timestampnow = formatter.parse(formatter.format(new Date())).getTime(); // todo more fancy
                    Log.debug("timestamp is :" + timestamp);

                    if (timestamp == timestampnow || timestamp == timestampnow + 86400000) { // 86400000 == one day
                        // valid time
                        // TODO: 12.01.20 read right waste type from db and replace below
                        tramsmitMessage(deviceid + "," + "Plastic" + "," + 1);
                        Log.debug("valid time");
                        return;
                    }
                } while (result.next());
                tramsmitMessage(deviceid + "," + "Plastic" + "," + 0); //transmit zero if not returned before
            }
        } catch (SQLException | ParseException e) {
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