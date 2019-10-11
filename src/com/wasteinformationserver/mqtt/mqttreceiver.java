package com.wasteinformationserver.mqtt;

import com.wasteinformationserver.basicutils.Log;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class mqttreceiver {

    private MqttClient client;
    public ArrayList<ActionListener> mylisteners = new ArrayList<>();
    public String message;

    public mqttreceiver() {

    }

    public String getmessage() {

         String temp;

        try {
            client = new MqttClient("tcp://192.168.65.15:1883", "JavaSample");
            client.connect();

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    message =new String(mqttMessage.getPayload());
                    notifylisteners(message);

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            client.subscribe("TopicIn");
            Log.debug("subscribed topic");
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return message;
    }


    private void notifylisteners(String message) {
        for (ActionListener ac : mylisteners) {
            ac.actionPerformed(new ActionEvent(this, 0, message));
        }
    }

    public void addMessageReceivedListener(ActionListener l) {
        mylisteners.add(l);
    }

}

