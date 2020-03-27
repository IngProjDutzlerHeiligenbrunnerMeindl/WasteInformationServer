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
        val obj = Storage()
        fun getInstance(): Storage {
            return obj;
        }
    }

    var mqttServer: String = "";


    /**
     * init config file
     */
    fun init() {
        val prop = Properties()
        try {
            // try to load existing config file
            val inp = FileInputStream("settings.prop")
            prop.load(inp)


        } catch (ee: FileNotFoundException) {
            // file not generated yet
            prop.store(FileOutputStream("settings.prop"), "")
            Log.info("new Settings config file generated")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}