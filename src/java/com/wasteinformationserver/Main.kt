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
    val stor = Storage.getInstance();
    JDBC.init(stor.dbUser, stor.dbPassword, stor.dbName, stor.dbhost, stor.dbPort)


    //startup web server
    val mythread = Thread(Runnable { Webserver().startserver() })
    mythread.start()


    //startup mqtt service
    Log.message("starting mqtt service")

    if (JDBC.isConnected()) {
        val m = MqttService(Storage.getInstance().mqttServer, Storage.getInstance().mqttPort.toString())
        m.startupService()
    }else{
        Log.error("could't start mqtt service because of missing db connection!")
    }
}