package com.wasteinformationserver.mqtt

import com.wasteinformationserver.basicutils.Log.Log.debug
import com.wasteinformationserver.basicutils.Log.Log.error
import com.wasteinformationserver.basicutils.Log.Log.info
import com.wasteinformationserver.basicutils.Log.Log.message
import com.wasteinformationserver.db.JDBC
import org.eclipse.paho.client.mqttv3.*
import java.io.IOException
import java.sql.SQLException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Mqtt Service to receive and send back messages to the Hardware
 * check values from db send right feedback to hardware.
 *
 * @author Lukas Heiligenbrunner
 * @author Gregor Dutzler
 */
class MqttService(serverurl: String, port: String) {
    private val serveruri: String = "tcp://$serverurl:$port"
    private var client: MqttClient = MqttClient(serveruri, "JavaSample42")
    private var db: JDBC = JDBC.getInstance()

    /**
     * init mqtt service
     * JDBC has to be inited before
     *
     * @param serverurl mqtt server ip or hostname
     * @param port      mqtt server port
     */
    init {
        connectToDb()
    }

    /**
     * startup of the mqtt service
     */
    fun startupService() {
        try {
            client = MqttClient(serveruri, "JavaSample42")
            val connOpts = MqttConnectOptions()
            connOpts.isCleanSession = true
            client.connect(connOpts)
            client.setCallback(object : MqttCallback {
                override fun connectionLost(throwable: Throwable) {
                    error("connection lost")
                    connectToDb()
                }

                override fun messageArrived(s: String, mqttMessage: MqttMessage) {
                    val deviceid = String(mqttMessage.payload)
                    message("received Request from PCB")
                    val res = db.executeQuery("SELECT * from devices WHERE DeviceID=$deviceid")
                    try {
                        res.last()
                        if (res.row != 0) { //existing device
                            res.first()
                            val devicecities = db.executeQuery("SELECT * from device_city WHERE DeviceID='$deviceid'")
                            devicecities.last()
                            if (devicecities.row == 0) { //not configured
                                tramsmitMessage("$deviceid,-1")
                            }
                            else {
                                devicecities.first()
                                devicecities.previous()

                                while (devicecities.next()) {
                                    val cityid = devicecities.getInt("CityID")
                                    checkDatabase(cityid, deviceid.toInt())
                                }
                            }
                        }
                        else { //new device
                            db.executeUpdate("INSERT INTO devices (DeviceID) VALUES ($deviceid)")
                            info("new device registered to server")
                            tramsmitMessage("$deviceid,-1")
                        }
                    } catch (e: SQLException) {
                        e.printStackTrace()
                    }
                }

                override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {}
            })
            client.subscribe("TopicIn")
        } catch (e: MqttException) {
            error("Connection to the Broker failed")
        }
    }

    private fun checkDatabase(citywastezoneid: Int, deviceid: Int) {
        var wastetype = -1
        val set2 = db.executeQuery("SELECT * FROM cities WHERE `id`='$citywastezoneid'")
        try {
            set2.last()
            if (set2.row != 1) { //error
            }
            else {
                val typ = set2.getString("wastetype")
                wastetype = getIntTyp(typ)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        val result = db.executeQuery("SELECT pickupdates.pickupdate FROM pickupdates WHERE pickupdates.citywastezoneid=$citywastezoneid")
        try {
            result.last()
            if (result.row == 0) { //if not found in db --> send zero
                debug("not found in db")
                tramsmitMessage("$deviceid,$wastetype,0")
            }
            else {
                debug(result.getString("pickupdate"))
                result.first()
                do {
                    val formatter = SimpleDateFormat("yyyy-MM-dd")
                    val timestamp = formatter.parse(result.getString("pickupdate")).time
                    val timestampnow = formatter.parse(formatter.format(Date())).time
                    debug("timestamp is :$timestamp")
                    if (timestamp == timestampnow || timestamp == timestampnow + 86400000) { // 86400000 == one day
                        // valid time
                        tramsmitMessage("$deviceid,$wastetype,1")
                        debug("valid time")
                        return
                    }
                } while (result.next())
                tramsmitMessage("$deviceid,$wastetype,0") //transmit zero if not returned before
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun tramsmitMessage(temp: String) {
        debug("sending message >>>$temp")
        val message = MqttMessage(temp.toByteArray())
        message.qos = 2
        try {
            client.publish("TopicOut", message)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private fun getTyp(number: Int): String? {
        return when (number) {
            1 -> "Plastic"
            2 -> "Metal"
            3 -> "Residual waste"
            4 -> "Biowaste"
            else -> null
        }
    }

    private fun getIntTyp(temp: String): Int {
        return when (temp) {
            "Plastic" -> 1
            "Metal" -> 2
            "Residual waste" -> 3
            "Biowaste" -> 4
            else -> 0
        }
    }

    private fun connectToDb() {
        db = JDBC.getInstance()
    }
}