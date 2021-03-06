package com.wasteinformationserver.website.datarequests

import com.wasteinformationserver.basicutils.Log.Log.debug
import com.wasteinformationserver.basicutils.Log.Log.error
import com.wasteinformationserver.db.JDBC
import com.wasteinformationserver.website.basicrequest.PostRequest
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

/**
 * Class for all requests on device Page
 *
 * @author Lukas Heiligenbrunner
 */
class DeviceRequest : PostRequest() {
    override fun request(params: HashMap<String, String>): String {
        if (!JDBC.isConnected()) {
            error("no connection to db")
            return "{\"query\" : \"nodbconn\"}"
        }
        val jdbc: JDBC = JDBC.getInstance()

        val sb = StringBuilder()
        val deviceset: ResultSet
        when (params["action"]) {
            /**
             * return all available devices
             */
            "getdevices" -> {
                deviceset = jdbc.executeQuery("SELECT * FROM `devices")
                sb.append("{\"data\":[")
                try {
                    while (deviceset.next()) {
                        val deviceid = deviceset.getInt("DeviceID")
                        val cityid = deviceset.getInt("CityID")
                        if (cityid == -1) {
                            sb.append("{\"deviceid\":").append(deviceid).append(",\"cityid\":").append(cityid).append("}")
                        }
                        else {
                            val devicename = deviceset.getString("DeviceName")
                            val devicelocation = deviceset.getString("DeviceLocation")
                            sb.append("{\"deviceid\":").append(deviceid).append(",\"devicename\":\"").append(devicename).append("\",\"devicelocation\":\"").append(devicelocation).append("\",\"devices\":[")
                            val devicecities = jdbc.executeQuery("SELECT * FROM `device_city` INNER JOIN `cities` ON device_city.CityID=cities.id WHERE `DeviceID`='$deviceid'")
                            while (devicecities.next()) {
                                val cityidd = devicecities.getInt("id")
                                val cityname = devicecities.getString("name")
                                val wastetype = devicecities.getString("wastetype")
                                val zone = devicecities.getString("zone")
                                sb.append("{\"cityid\":").append(cityidd).append(",\"cityname\":\"").append(cityname).append("\",\"wastetype\":\"").append(wastetype).append("\",\"zone\":\"").append(zone).append("\"}")
                                if (!devicecities.isLast) {
                                    sb.append(",")
                                }
                            }
                            sb.append("]}")
                        }
                        if (!deviceset.isLast) {
                            sb.append(",")
                        }
                    }
                    sb.append("]}")
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
            /**
             * returns all available city names
             */
            "getCitynames" -> {
                deviceset = jdbc.executeQuery("select * from cities")
                debug(deviceset.toString())
                sb.append("{")
                try {
                    var prev = ""
                    while (deviceset.next()) {
                        if (prev != deviceset.getString("name")) {
                            if (!deviceset.isFirst) {
                                sb.append(",")
                            }
                            sb.append("\"").append(deviceset.getString("name")).append("\":\"").append(deviceset.getString("name")).append("\"")
                        }
                        prev = deviceset.getString("name")
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                sb.append("}")
                debug(sb.toString())
            }
            /**
             * returns all available zones for specified city
             */
            "getzones" -> {
                deviceset = jdbc.executeQuery("select * from cities WHERE `name`='" + params["cityname"] + "' ORDER BY zone ASC")
                debug(deviceset.toString())
                sb.append("{")
                try {
                    var prev = 42
                    while (deviceset.next()) {
                        if (prev != deviceset.getInt("zone")) {
                            sb.append("\"").append(deviceset.getInt("zone")).append("\":\"").append(deviceset.getInt("zone")).append("\"")
                            if (!deviceset.isLast) {
                                sb.append(",")
                            }
                        }
                        prev = deviceset.getInt("zone")
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                sb.append("}")
            }
            /**
             * returns all available waste types for specified zone and city
             */
            "gettypes" -> {
                deviceset = jdbc.executeQuery("select * from cities WHERE `name`='" + params["cityname"] + "' AND `zone`='" + params["zonename"] + "' ORDER BY zone ASC")
                debug(deviceset.toString())
                sb.append("{")
                try {
                    var prev = "42"
                    while (deviceset.next()) {
                        if (prev != deviceset.getString("wastetype")) {
                            sb.append("\"" + deviceset.getString("wastetype") + "\":\"" + deviceset.getString("wastetype") + "\"")
                            if (!deviceset.isLast) {
                                sb.append(",")
                            }
                        }
                        prev = deviceset.getString("wastetype")
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                sb.append("}")
            }
            /**
             * configure device and save infos to db
             */
            "savetodb" -> try {
                val cityset = jdbc.executeQuery("SELECT id from cities WHERE `name`='" + params["cityname"] + "' AND `zone`='" + params["zonename"] + "' AND `wastetype`='" + params["wastetype"] + "'")
                cityset.last()
                if (cityset.row != 1) {
                    error("error saving device to db --> device multiply defined?")
                    sb.append("{\"success\":\"false\"}")
                }
                else {
                    val cityid = cityset.getInt("id")
                    jdbc.executeUpdate("INSERT INTO `device_city` (`DeviceID`, `CityID`) VALUES ('" + params["deviceid"] + "', '" + cityid + "');")
                    jdbc.executeUpdate("UPDATE devices SET `CityID`='0',`DeviceName`='" + params["devicename"] + "',`DeviceLocation`='" + params["devicelocation"] + "' WHERE `DeviceID`='" + params["deviceid"] + "'")
                    sb.append("{\"success\":\"true\"}")
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
            /**
             * delete a configured device from db
             */
            "deleteDevice" -> {
                try {
                    jdbc.executeUpdate("DELETE FROM devices WHERE `DeviceID`='" + params["id"] + "'")
                    jdbc.executeUpdate("DELETE FROM device_city WHERE `DeviceID`='" + params["id"] + "'")
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                sb.append("{\"status\":\"success\"}")
            }
            /**
             * add new city/zone/type to db to existing one
             */
            "addtodb" -> {
                try {
                    val device = jdbc.executeQuery("SELECT * FROM cities WHERE name='" + params["cityname"] + "' AND wastetype='" + params["wastetype"] + "' AND zone='" + params["zonename"] + "'")
                    device.first()
                    val cityid = device.getInt("id")
                    jdbc.executeUpdate("INSERT INTO `device_city` (`DeviceID`, `CityID`) VALUES ('" + params["deviceid"] + "', '" + cityid + "');")
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                sb.append("{\"success\":true}")
            }
            /**
             * return header information such as devicenumber and number of unconfigured devices
             */
            "getheader" -> {
                try {
                    var numberset = jdbc.executeQuery("SELECT * FROM devices")
                    numberset.last()
                    val devicenr = numberset.row

                    numberset = jdbc.executeQuery("SELECT * FROM devices WHERE CityID=-1")
                    numberset.last()
                    val unconfigureddevices = numberset.row

                    sb.append("{\"success\":true,\"devicenumber\":$devicenr, \"unconfigureddevices\":$unconfigureddevices}")
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
        }
        return sb.toString()
    }
}