package com.wasteinformationserver.basicutils

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * Storage of user information
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

    var mqttServer: String = ""
        get() = field
        set(value) {
            field = value
        }

    var mqttPort: Int = -1
        get() = field
        set(value) {
            field = value
        }

     var dbName: String = ""
        get() = field
        set(value) {
            field = value
        }

    var dbhost: String = ""
        get() = field
        set(value) {
            field = value
        }

     var dbUser: String = ""
        get() = field
        set(value) {
            field = value
        }

     var dbPassword: String = ""
        get() = field
        set(value) {
            field = value
        }

     var dbPort: Int = -1
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
            dbhost = prop["dbhost"] as String
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
        prop["dbhost"] = dbhost
        prop["dbname"] = dbName
        prop["dbuser"] = dbUser
        prop["dbpass"] = dbPassword
        prop["dbport"] = dbPort.toString()

        prop.store(FileOutputStream("settings.prop"), "main config")
    }

    /**
     * check if all needed properties are set up correctly
     * todo real check if connections can be established
     */
    fun isEveryThingDefined(): Boolean {
        return (isMqttServerDefined() &&
                isMqttPortDefined() &&
                isDBNameDefined() &&
                isDBUsernameDefined() &&
                isDBPasswdDefined() &&
                isDBPortDefined())
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