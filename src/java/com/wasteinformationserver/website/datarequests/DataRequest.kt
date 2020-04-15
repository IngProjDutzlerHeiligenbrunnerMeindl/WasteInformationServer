package com.wasteinformationserver.website.datarequests

import com.wasteinformationserver.basicutils.Info
import com.wasteinformationserver.basicutils.Log.Log.debug
import com.wasteinformationserver.basicutils.Log.Log.error
import com.wasteinformationserver.basicutils.Log.Log.warning
import com.wasteinformationserver.db.JDBC
import com.wasteinformationserver.website.basicrequest.PostRequest
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.SQLIntegrityConstraintViolationException
import java.text.SimpleDateFormat
import java.util.*

/**
 * General Datarequests for Dashboard
 *
 * @author Lukas Heiligenbrunner
 */
class DataRequest : PostRequest() {
    override fun request(params: HashMap<String, String>): String {
        val sb = StringBuilder()
        var set: ResultSet?
        var status = -1

        if (!JDBC.isConnected()) {
            error("no connection to db")
            return "{\"query\" : \"nodbconn\"}"
        }
        val jdbc: JDBC = JDBC.getInstance()

        when (params["action"]) {
            /**
             * create a new city entry in db
             */
            "newCity" -> {
                sb.append("{")
                debug(params.toString())
                //                check if wastezone and wasteregion already exists
                debug(params["cityname"] + params["wastetype"] + params["wastezone"])
                set = jdbc.executeQuery("select * from `cities` where `name`='" + params["cityname"] + "' AND `wastetype`='" + params["wastetype"] + "' AND `zone`='" + params["wastezone"] + "'")
                var size = 0
                try {
                    if (set != null) {
                        set.last() // moves cursor to the last row
                        size = set.row // get row id
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                if (size == 0) { //doesnt exist
                    try {
                        status = jdbc.executeUpdate("INSERT INTO `cities`(`userid`, `name`, `wastetype`, `zone`) VALUES ('0','" + params["cityname"] + "','" + params["wastetype"] + "','" + params["wastezone"] + "');")
                    } catch (e: SQLException) {
                        e.printStackTrace()
                    }
                    if (status == 1) {
                        sb.append("\"status\" : \"inserted\"")
                    }
                    else {
                        sb.append("\"status\" : \"inserterror\"")
                    }
                }
                else if (size > 1) {
                    warning("more than one entry in db!!!")
                    sb.append("\"status\" : \"exists\"")
                }
                else { //already exists
                    sb.append("\"status\" : \"exists\"")
                }
                sb.append(",\"query\":\"ok\"")
                sb.append("}")
            }
            /**
             * return all defined cities from db
             */
            "getAllCities" -> {
                set = jdbc.executeQuery("select * from cities")
                debug(set.toString())
                sb.append("{\"data\":[")
                try {
                    while (set.next()) {
                        sb.append("{\"cityname\":\"" + set.getString("name") + "\"")
                        sb.append(",\"wastetype\":\"" + set.getString("wastetype") + "\"")
                        sb.append(",\"id\":\"" + set.getString("id") + "\"")
                        sb.append(",\"zone\":\"" + set.getString("zone") + "\"}")
                        if (!set.isLast) {
                            sb.append(",")
                        }
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                sb.append("]")
                sb.append(",\"query\":\"ok\"")
                sb.append("}")
            }
            /**
             * delete a specific city
             */
            "deletecity" -> {
                //DELETE FROM `cities` WHERE `id`=0
                sb.append("{")
                try {
                    status = jdbc.executeUpdate("DELETE FROM `cities` WHERE `id`='" + params["id"] + "'")
                    if (status == 1) { //success
                        sb.append("\"status\" : \"success\"")
                    }
                    else {
                        sb.append("\"status\" : \"error\"")
                    }
                } catch (e: SQLIntegrityConstraintViolationException) {
                    warning("dependencies of deletion exist")
                    sb.append("\"status\" : \"dependenciesnotdeleted\"")
                } catch (e: SQLException) {
                    error("sql exception: " + e.message)
                    sb.append("\"status\" : \"error\"")
                }
                debug(status)
                sb.append(",\"query\":\"ok\"")
                sb.append("}")
            }
            /**
             * returns all configured dates with its city and zone
             */
            "getAllDates" -> {
                set = jdbc.executeQuery("SELECT pickupdates.id,pickupdates.pickupdate,cities.userid,cities.name,cities.wastetype,cities.zone " +
                        "FROM `pickupdates` INNER JOIN `cities` ON pickupdates.citywastezoneid = cities.id")
                sb.append("{\"data\":[")
                try {
                    while (set.next()) {
                        sb.append("{\"date\":\"" + set.getString("pickupdate") + "\"")
                        sb.append(",\"cityname\":\"" + set.getString("name") + "\"")
                        sb.append(",\"wastetype\":\"" + set.getString("wastetype") + "\"")
                        sb.append(",\"id\":\"" + set.getString("id") + "\"")
                        sb.append(",\"zone\":\"" + set.getString("zone") + "\"}")
                        if (!set.isLast) {
                            sb.append(",")
                        }
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                sb.append("]")
                sb.append(",\"query\":\"ok\"")
                sb.append("}")
            }
            /**
             * delete a specific date
             */
            "deletedate" -> {
                sb.append("{")
                try {
                    status = jdbc.executeUpdate("DELETE FROM `pickupdates` WHERE `id`='" + params["id"] + "'")
                    if (status == 1) { //success
                        sb.append("\"status\" : \"success\"")
                    }
                    else {
                        sb.append("\"status\" : \"error\"")
                    }
                } catch (e: SQLIntegrityConstraintViolationException) {
                    warning("dependencies of deletion exist")
                    sb.append("\"status\" : \"dependenciesnotdeleted\"")
                } catch (e: SQLException) {
                    error("sql exception: " + e.message)
                    sb.append("\"status\" : \"error\"")
                }
                sb.append(",\"query\":\"ok\"")
                sb.append("}")
            }
            /**
             * return version foot data
             */
            "getversionandbuildtime" -> {
                sb.append("{")
                sb.append("\"version\" : \"" + Info.getVersion() + "\"")
                sb.append(",\"buildtime\" : \"" + Info.getBuilddate() + "\"")
                sb.append(",\"query\":\"ok\"")
                sb.append("}")
            }
            /**
             * return head data with basic collection infos
             */
            "getStartHeaderData" -> {
                sb.append("{")
                try {
                    var sdf = SimpleDateFormat("yyyy-MM-dd")
                    var date = Date()
                    var time = sdf.format(date)
                    set = jdbc.executeQuery("SELECT * FROM `pickupdates` WHERE `pickupdate` BETWEEN  '0000-12-27' AND '$time'")
                    set.last()
                    sb.append("\"finshedcollections\":\"" + set.row + "\"")
                    sdf = SimpleDateFormat("yyyy-MM-dd")
                    date = Date()
                    date = Date(date.time + 1 * 24 * 60 * 60 * 1000)
                    time = sdf.format(date)
                    set = jdbc.executeQuery("SELECT * FROM `pickupdates` WHERE `pickupdate` BETWEEN '$time' AND '2222-12-27'")
                    set.last()
                    sb.append(",\"futurecollections\":\"" + set.row + "\"")
                    set = jdbc.executeQuery("select * from pickupdates")
                    set.last()
                    sb.append(",\"collectionnumber\":\"" + set.row + "\"")
                    set = jdbc.executeQuery("select * from `cities`")
                    set.last()
                    sb.append(",\"citynumber\":\"" + set.row + "\"")
                } catch (e: SQLException) {
                    error("sql exception: " + e.message)
                    sb.append("\"status\" : \"error\"")
                }
                sb.append(",\"query\":\"ok\"")
                sb.append("}")
            }
        }
        return sb.toString()
    }
}