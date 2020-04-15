@file:JvmName("Main")
package com.wasteinformationserver

import com.wasteinformationserver.basicutils.Info
import com.wasteinformationserver.basicutils.Log
import com.wasteinformationserver.basicutils.Storage
import com.wasteinformationserver.db.JDBC
import com.wasteinformationserver.mqtt.MqttService
import com.wasteinformationserver.website.Webserver
import java.io.IOException

/**
 * application entry point
 *
 * @author Lukas Heiligenbrunner
 */
fun main() {
    Log.setLevel(Log.DEBUG)
    Info.init()
    Storage.getInstance().init()

    Log.info("startup of WasteInformationServer")

    Runtime.getRuntime().addShutdownHook(Thread(Runnable {
        try {
            Thread.sleep(200)
            Log.warning("Shutting down ...")
            JDBC.getInstance().disconnect();
            //shutdown routine
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }))

    Log.info("Server version: " + Info.getVersion())
    Log.debug("Build date: " + Info.getBuilddate())

    //initial connect to db
    Log.message("initial login to db")
    try {
        val stor = Storage.getInstance();
        JDBC.init(stor.dbUser, stor.dbPassword, stor.dbName, stor.dbhost, stor.dbPort)
//        JDBC.init("ingproject", "Kb9Dxklumt76ieq6", "ingproject", "db.power4future.at", 3306)
        //JDBC.init("users", "kOpaIJUjkgb9ur6S", "wasteinformation", "192.168.65.15", 3306);
    } catch (e: IOException) {
        Log.error("no connection to db")
    }


    //startup web server
    val mythread = Thread(Runnable { Webserver().startserver() })
    mythread.start()


    //startup mqtt service
    Log.message("starting mqtt service")

    if (JDBC.isConnected()) {
        val m = MqttService(Storage.getInstance().mqttServer, Storage.getInstance().mqttPort.toString())
        //    val m = MqttService("mqtt.heili.eu", "1883")
        m.startupService()
    }else{
        Log.error("could't start mqtt service because of missing db connection!")
    }

}