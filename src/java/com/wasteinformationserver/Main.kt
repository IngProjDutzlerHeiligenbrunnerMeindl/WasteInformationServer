package com.wasteinformationserver

import com.wasteinformationserver.basicutils.Info
import com.wasteinformationserver.basicutils.Log
import com.wasteinformationserver.db.JDBC
import com.wasteinformationserver.mqtt.MqttService
import com.wasteinformationserver.website.Webserver
import java.io.IOException

fun main() {
    Log.Log.setLevel(Log.Log.DEBUG)
    Info.init()

    Log.Log.info("startup of WasteInformationServer")

    Runtime.getRuntime().addShutdownHook(Thread(Runnable {
        try {
            Thread.sleep(200)
            Log.Log.warning("Shutting down ...")
            //shutdown routine
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }))

    Log.Log.info("Server version: " + Info.getVersion())
    Log.Log.debug("Build date: " + Info.getBuilddate())

    //initial connect to db
    Log.Log.message("initial login to db")
    try {
        JDBC.init("ingproject", "Kb9Dxklumt76ieq6", "ingproject", "db.power4future.at", 3306)
        //JDBC.init("users", "kOpaIJUjkgb9ur6S", "wasteinformation", "192.168.65.15", 3306);
    } catch (e: IOException) { //e.printStackTrace();
        Log.Log.error("no connection to db")
    }


    //startup web server
    val mythread = Thread(Runnable { Webserver().startserver() })
    mythread.start()


    //startup mqtt service
    Log.Log.message("starting mqtt service")

    val m = MqttService("mqtt.heili.eu", "1883")
    m.startupService()
}