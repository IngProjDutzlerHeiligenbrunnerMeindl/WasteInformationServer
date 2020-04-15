package com.wasteinformationserver.website.datarequests.login

import com.wasteinformationserver.basicutils.Log.Log.debug
import com.wasteinformationserver.basicutils.Log.Log.error
import com.wasteinformationserver.basicutils.Log.Log.message
import com.wasteinformationserver.basicutils.Storage
import com.wasteinformationserver.db.JDBC
import com.wasteinformationserver.website.HttpTools.Companion.stringToMD5
import com.wasteinformationserver.website.basicrequest.PostRequest
import java.io.IOException
import java.sql.SQLException
import java.util.*

/**
 * request handler of new user login requests
 * - checks the truth of username and password
 * - replies right error messages or the success login
 *
 * @author Lukas Heiligenbrunner
 */
class LoginRequest : PostRequest() {
    override fun request(params: HashMap<String, String>): String {
        message("new login request")
        val password = params["password"]
        val username = params["username"]
        val jdbc: JDBC = try {
            JDBC.getInstance()
        } catch (e: Exception) {
            error("no connection to db" + e.printStackTrace())
            return "{\"status\" : \"nodbconn\"}"
        }

        if (!Storage.getInstance().isEveryThingDefined()) {
            error("config not configured correctly")
            return "{\"status\" : \"conferror\"}"
        }

        val s = jdbc.executeQuery("select * from user where username ='$username'")

        debug("successfully logged in to db")
        var response = "{\"accept\": false}"
        try {
            s.last()
            if (s.row == 1) {
                //success
                if (StringToMD5(password!!) == s.getString("password")) {
                    debug("login success")
                    LoginState.getObject().logIn()
                    LoginState.getObject().setAccountData(username, s.getString("firstName"), s.getString("secondName"), s.getString("email"), s.getInt("permission"))
                    response = "{\"accept\": true}"
                }
                else {
                    debug("wrong password")
                }
            }
            else if (s.row == 0) {
                //user not found
                debug("user not found")
            }
            else {
                //internal error two users with same name...?
                error("there seem to be two users with same name")
            }
            debug("rowcount: " + s.row)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return response
    }
}