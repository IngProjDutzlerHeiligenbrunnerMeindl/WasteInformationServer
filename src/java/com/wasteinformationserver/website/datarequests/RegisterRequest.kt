package com.wasteinformationserver.website.datarequests

import com.wasteinformationserver.basicutils.Log
import com.wasteinformationserver.basicutils.Log.Log.debug
import com.wasteinformationserver.db.JDBC
import com.wasteinformationserver.website.HttpTools.Companion.stringToMD5
import com.wasteinformationserver.website.basicrequest.PostRequest
import java.io.IOException
import java.sql.SQLException
import java.util.*

class RegisterRequest : PostRequest() {
    override fun request(params: HashMap<String, String>): String {
        debug(params.toString())
        val passhash = stringToMD5(params["password"]!!)
        val reply: StringBuilder = StringBuilder("{")
        try {
            val myjd: JDBC = JDBC.getInstance()

            val status = myjd.executeUpdate("INSERT INTO `user` (`username`, `firstName`, `secondName`, `password`, `email`, `logindate`) VALUES ('" + params["username"] + "', '" + params["firstname"] + "', '" + params["lastname"] + "', '" + passhash + "', '" + params["email"] + "', current_timestamp());")
            if (status == 1) {
                reply.append("\"accept\": true")
            }
            else {
                reply.append("\"accept\": false")
            }

        } catch (e: IOException) {
            Log.error("no connection to db")
            reply.append("\"accept\": false")
        } catch (e: SQLException) {
            e.printStackTrace()
            reply.append("\"accept\": false")
        }
        reply.append("}")

        return reply.toString()
    }
}