package com.wasteinformationserver.website.datarequests

import com.wasteinformationserver.basicutils.Log.Log.debug
import com.wasteinformationserver.basicutils.Log.Log.error
import com.wasteinformationserver.basicutils.Log.Log.warning
import com.wasteinformationserver.db.JDBC
import com.wasteinformationserver.website.basicrequest.PostRequest
import java.sql.ResultSet
import java.sql.SQLException

/**
 * todo
 *
 * @author Lukas Heiligenbrunner
 */
class NewDateRequest : PostRequest() {
    override fun request(params: HashMap<String, String>): String {
        val sb = StringBuilder()
        val set: ResultSet

        if (!JDBC.isConnected()) {
            error("no connection to db")
            return "{\"query\" : \"nodbconn\"}"
        }
        val jdbc: JDBC = JDBC.getInstance()

        when (params["action"]) {
            "getCitynames" -> {
                set = jdbc.executeQuery("select * from cities")
                debug(set.toString())
                sb.append("{\"data\":[")
                try {
                    var prev = ""
                    while (set.next()) {
                        if (prev != set.getString("name")) { // not same --> new element
                            if (!set.isFirst) {
                                sb.append(",")
                            }
                            sb.append("{\"cityname\":\"" + set.getString("name") + "\"}")
                        }
                        prev = set.getString("name")
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                sb.append("]")
                sb.append(",\"query\":\"ok\"")
                sb.append("}")
                debug(sb.toString())
            }
            "getzones" -> {
                set = jdbc.executeQuery("select * from cities WHERE `name`='" + params["cityname"] + "' ORDER BY zone ASC")
                debug(set.toString())
                sb.append("{\"data\":[")
                try {
                    var prev = 42
                    while (set.next()) {
                        if (prev != set.getInt("zone")) { // not same --> append next
                            sb.append("{\"zone\":\"" + set.getInt("zone") + "\"}")
                            if (!set.isLast) {
                                sb.append(",")
                            }
                        }
                        prev = set.getInt("zone")
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                sb.append("]")
                sb.append(",\"query\":\"ok\"")
                sb.append("}")
            }
            "gettypes" -> {
                set = jdbc.executeQuery("select * from cities WHERE `name`='" + params["cityname"] + "' AND `zone`='" + params["zonename"] + "' ORDER BY zone ASC")
                debug(set.toString())
                sb.append("{\"data\":[")
                try {
                    var prev = "42"
                    while (set.next()) {
                        if (prev !== set.getString("wastetype")) {
                            sb.append("{\"wastetype\":\"" + set.getString("wastetype") + "\"}")
                            if (!set.isLast) {
                                sb.append(",")
                            }
                        }
                        prev = set.getString("wastetype")
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                sb.append("]")
                sb.append(",\"query\":\"ok\"")
                sb.append("}")
            }
            "newdate" -> {
                sb.append("{")
                debug(params)
                set = jdbc.executeQuery("select * from cities WHERE `name`='" + params["cityname"] + "' AND `zone`='" + params["zone"] + "' AND `wastetype`='" + params["wastetype"] + "'")
                try {
                    set.last()
                    if (set.row == 1) {
                        debug(set.getInt("id"))
                        val status = jdbc.executeUpdate("INSERT INTO `pickupdates`(`citywastezoneid`, `pickupdate`) VALUES ('" + set.getInt("id") + "','" + params["date"] + "')")
                        if (status == 1) {
                            sb.append("\"status\" : \"success\"")
                        }
                        else {
                            sb.append("\"status\" : \"error\"")
                        }
                    }
                    else {
                        warning("city doesnt exist!")
                        sb.append("\"status\" : \"citydoesntexist\"")
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                sb.append(",\"query\":\"ok\"")
                sb.append("}")
            }
        }
        return sb.toString()
    }
}