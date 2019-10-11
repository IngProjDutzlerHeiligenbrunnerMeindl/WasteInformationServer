package com.wasteinformationserver.mqtt;

import com.wasteinformationserver.basicutils.Log;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;

public class mqtttransmitter {

    private String messagedatabase;

    public mqtttransmitter() {
    }

    public void sendmessage(String temp) {
        String topic = "TopicOut";
        String content = temp;
        int qos = 2;
        String broker = "tcp://192.168.65.15:1883";
        String clientId = "JavaSample";
        MemoryPersistence persistence = new MemoryPersistence();


        try {

            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            Log.debug("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            Log.debug("Connected");
            Log.debug("Publishing message: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            Log.debug("Message published");
            sampleClient.disconnect();
            Log.debug("Disconnected");


        } catch (MqttException me) {
            Log.debug("reason " + me.getReasonCode());
            Log.debug("msg " + me.getMessage());
            Log.debug("loc " + me.getLocalizedMessage());
            Log.debug("cause " + me.getCause());
            Log.debug("excep " + me);
            me.printStackTrace();
        }
    }

}