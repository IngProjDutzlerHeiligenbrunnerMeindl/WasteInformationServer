package com.wasteinformationserver.mqtt;

import com.wasteinformationserver.basicutils.Log;
import org.eclipse.paho.client.mqttv3.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class mqttreceiver {

    private MqttClient client;
    public ArrayList<ActionListener> mylisteners = new ArrayList<>();
    public String message;

    public mqttreceiver(MqttClient mqtt) {
        this.client = mqtt;
    }

    public String getmessage() {

        try {
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    Log.error("connection lost");
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    message = new String(mqttMessage.getPayload());
                    notifylisteners(message);

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            client.subscribe("TopicIn");
            Log.debug("subscribed topic");
        } catch (MqttException e) {
            Log.error("Connection to the ESB failed");
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

