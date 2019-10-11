package com.wasteinformationserver.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class mqttreceiver {

    MqttClient client;
    String message;

    ArrayList<ActionListener> mylisteners = new ArrayList<>();

    public mqttreceiver() {

        try {
            client = new MqttClient("tcp://192.168.65.15:1883", "JavaSample");
            client.connect();

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    System.out.println(new String(mqttMessage.getPayload()));
                    notifylisteners(new String(mqttMessage.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            client.subscribe("test/topic");
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    private void notifylisteners(String message) {
        for (ActionListener ac : mylisteners) {
            new ActionEvent(this, 0, message);
        }
    }

    public void addMessageReceivedListener(ActionListener l) {
        mylisteners.add(l);
    }
}
