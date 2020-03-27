package com.wasteinformationserver.basicutils

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * Storeage of user information
 * * database infos
 * * mqtt infos
 *
 * @author Lukas Heiligenbrunner
 */
class Storage {
    companion object {
        private val obj = Storage()
        fun getInstance(): Storage {
            return obj;
        }
    }

    private var mqttServer: String = ""
        get() = field
        set(value) {
            field = value
        }

    private var mqttPort: Int = -1
        get() = field
        set(value) {
            field = value
        }

    private var dbName: String = ""
        get() = field
        set(value) {
            field = value
        }

    private var dbUser: String = ""
        get() = field
        set(value) {
            field = value
        }

    private var dbPassword: String = ""
        get() = field
        set(value) {
            field = value
        }

    private var dbPort: Int = -1
        get() = field
        set(value) {
            field = value
        }

    private val prop = Properties()

    /**
     * init config file
     */
    fun init() {
        try {
            // try to load existing config file
            val inp = FileInputStream("settings.prop")
            prop.load(inp)

            mqttServer = prop["mqttserver"] as String
            mqttPort = (prop["mqttport"] as String).toInt()
            dbName = prop["dbname"] as String
            dbUser = prop["dbuser"] as String
            dbPassword = prop["dbpass"] as String
            dbPort = (prop["dbport"] as String).toInt()
        } catch (ee: FileNotFoundException) {
            // file not generated yet
            store()
            Log.info("new Settings config file generated")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * store data to storage file
     */
    fun store() {
        prop["mqttserver"] = mqttServer
        prop["mqttport"] = mqttPort.toString()
        prop["dbname"] = dbName
        prop["dbuser"] = dbUser
        prop["dbpass"] = dbPassword
        prop["dbport"] = dbPort.toString()

        prop.store(FileOutputStream("settings.prop"), "")
    }

    /**
     * check if all needed properties are set up correctly
     */
    fun isEveryThingDefined(): Boolean {
        return (mqttServer != "" &&
                mqttPort != 0 &&
                dbName != "" &&
                dbUser != "" &&
                dbPassword != "" &&
                dbPort != -1)
    }


    /**
     * is the mqttservername defined?
     */
    fun isMqttServerDefined(): Boolean {
        return (mqttServer != "")
    }

    /**
     * is the mqttserver port defined?
     */
    fun isMqttPortDefined(): Boolean {
        return (mqttPort != -1)
    }

    /**
     * is the dbname  defined?
     */
    fun isDBNameDefined(): Boolean {
        return (dbName != "")
    }

    /**
     * is the dbport  defined?
     */
    fun isDBPortDefined(): Boolean {
        return (dbPort != -1)
    }

    /**
     * is the dbpassword  defined?
     */
    fun isDBPasswdDefined(): Boolean {
        return (dbPassword != "")
    }

    /**
     * is the dbusername  defined?
     */
    fun isDBUsernameDefined(): Boolean {
        return (dbUser != "")
    }
}