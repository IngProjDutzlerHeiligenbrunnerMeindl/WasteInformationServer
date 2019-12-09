package com.wasteinformationserver.mqtt;

import com.wasteinformationserver.basicutils.Log;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class mqtttransmitter {
    MqttClient client;

    public mqtttransmitter(MqttClient client) {
        this.client = client;
    }

    public void sendmessage(String temp) {
        String topic = "TopicOut";
        String content = temp;
        int qos = 2;
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            Log.debug("Connected");
            Log.debug("Publishing message: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            client.publish(topic, message);
            Log.debug("Message published");


        } catch (MqttException me) {
            Log.debug("reason " + me.getReasonCode());
            Log.debug("msg " + me.getMessage());
            Log.debug("loc " + me.getLocalizedMessage());
            Log.debug("cause " + me.getCause());
            Log.debug("excep " + me);
        }
    }
}