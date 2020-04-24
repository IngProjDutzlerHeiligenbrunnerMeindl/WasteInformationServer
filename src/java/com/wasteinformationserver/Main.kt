@file:JvmName("Main")

package com.wasteinformationserver

import com.wasteinformationserver.basicutils.Info
import com.wasteinformationserver.basicutils.Log
import com.wasteinformationserver.basicutils.Storage
import com.wasteinformationserver.db.JDBC
import com.wasteinformationserver.mqtt.MqttService
import com.wasteinformationserver.website.Webserver

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
        // shutdown routine
        Log.warning("Shutting down ...")
        JDBC.getInstance().disconnect();
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

    val m = MqttService.getInstance()
    m.init(Storage.getInstance().mqttServer, Storage.getInstance().mqttPort.toString())
    m.startupService()
}