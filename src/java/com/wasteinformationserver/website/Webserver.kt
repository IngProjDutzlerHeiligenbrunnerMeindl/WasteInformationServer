package com.wasteinformationserver.website

import com.sun.net.httpserver.HttpServer
import com.wasteinformationserver.basicutils.Log.Log.criticalerror
import com.wasteinformationserver.basicutils.Log.Log.info
import com.wasteinformationserver.website.datarequests.*
import com.wasteinformationserver.website.datarequests.login.CheckLoginState
import com.wasteinformationserver.website.datarequests.login.LoginRequest
import java.io.IOException
import java.net.BindException
import java.net.InetSocketAddress

/**
 * class to create the website nodes at specific paths
 *
 * @author Lukas Heiligenbrunner
 */
class Webserver {
    fun startserver() {
        info("starting Webserver")
        try {
            val server = HttpServer.create(InetSocketAddress(8000), 0)
            server.createContext("/", MainPage())
            server.createContext("/senddata/loginget", LoginRequest())
            server.createContext("/senddata/registerpost", RegisterRequest())
            server.createContext("/senddata/checkloginstate", CheckLoginState())
            server.createContext("/senddata/wastedata", DataRequest())
            server.createContext("/senddata/admindata", AdminRequests())
            server.createContext("/senddata/newdate", NewDateRequest())
            server.createContext("/senddata/Devicedata", DeviceRequest())
            server.executor = null // creates a default executor
            server.start()
            info("Server available at http://127.0.0.1:8000 now")
        } catch (e: BindException) {
            criticalerror("The Port 8000 is already in use!")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}