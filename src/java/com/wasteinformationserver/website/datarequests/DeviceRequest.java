package com.wasteinformationserver.website.datarequests;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDBC;
import com.wasteinformationserver.website.basicrequest.PostRequest;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DeviceRequest extends PostRequest {
    @Override
    public String request(HashMap<String, String> params) {

        JDBC jdbc = null;
        try {
            jdbc = jdbc.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        switch (params.get("action")) {
            case "getdevices":
                ResultSet setunconfigured = jdbc.executeQuery("SELECT * FROM `devices` WHERE `CityID`=-1");
                ResultSet setconfigured = jdbc.executeQuery("SELECT * FROM `devices`  INNER JOIN `cities` ON devices.CityID = cities.id");

                sb.append("{\"data\":[");
                try {
                    setconfigured.last();
                    int configsize = setconfigured.getRow();
                    setconfigured.first();
                    setconfigured.previous();

                    while (setunconfigured.next()) {
                        int deviceid = setunconfigured.getInt("DeviceID");
                        int cityid = setunconfigured.getInt("CityID");

                        sb.append("{\"deviceid\":\"" + deviceid + "\",\"cityid\":\"" + cityid + "\"}");

                        if (!(setunconfigured.isLast() && configsize == 0)) {
                            sb.append(",");
                        }
                    }

                    while (setconfigured.next()) {
                        int deviceid = setconfigured.getInt("DeviceID");
                        int cityid = setconfigured.getInt("CityID");

                        String devicename = setconfigured.getString("DeviceName");
                        String devicelocation = setconfigured.getString("DeviceLocation");

                        String cityname = setconfigured.getString("name");
                        String wastetype = setconfigured.getString("wastetype");
                        String zone = setconfigured.getString("zone");

                        sb.append("{\"deviceid\":\"" + deviceid + "\",\"cityid\":\"" + cityid + "\",\"devicename\":\"" + devicename + "\",\"devicelocation\":\"" + devicelocation + "\",\"cityname\":\"" + cityname + "\",\"wastetype\":\"" + wastetype + "\",\"zone\":\"" + zone + "\"}");

                        if (!setconfigured.isLast()) {
                            sb.append(",");
                        }
                    }
                    sb.append("]}");
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                break;
            case "getCitynames":
                setunconfigured = jdbc.executeQuery("select * from cities");
                Log.debug(setunconfigured.toString());
                sb.append("{");
                try {
                    String prev = "";
                    while (setunconfigured.next()) {
                        if (prev.equals(setunconfigured.getString("name"))) {

                        } else {
                            if (!setunconfigured.isFirst()) {
                                sb.append(",");
                            }
                            sb.append("\"" + setunconfigured.getString("name") + "\":\"" + setunconfigured.getString("name") + "\"");
                        }
                        prev = setunconfigured.getString("name");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sb.append("}");
                Log.debug(sb.toString());
                break;
            case "getzones":
                setunconfigured = jdbc.executeQuery("select * from cities WHERE `name`='" + params.get("cityname") + "' ORDER BY zone ASC");
                Log.debug(setunconfigured.toString());
                sb.append("{");
                try {
                    int prev = 42;
                    while (setunconfigured.next()) {
                        if (prev == setunconfigured.getInt("zone")) {

                        } else {
                            sb.append("\"" + setunconfigured.getInt("zone") + "\":\"" + setunconfigured.getInt("zone") + "\"");
                            if (!setunconfigured.isLast()) {
                                sb.append(",");
                            }
                        }
                        prev = setunconfigured.getInt("zone");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sb.append("}");
                break;
            case "gettypes":
                setunconfigured = jdbc.executeQuery("select * from cities WHERE `name`='" + params.get("cityname") + "' AND `zone`='" + params.get("zonename") + "' ORDER BY zone ASC");
                Log.debug(setunconfigured.toString());
                sb.append("{");
                try {
                    String prev = "42";
                    while (setunconfigured.next()) {
                        if (prev == setunconfigured.getString("wastetype")) {

                        } else {
                            sb.append("\"" + setunconfigured.getString("wastetype") + "\":\"" + setunconfigured.getString("wastetype") + "\"");
                            if (!setunconfigured.isLast()) {
                                sb.append(",");
                            }
                        }
                        prev = setunconfigured.getString("wastetype");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sb.append("}");
                break;
            case "savetodb":
                setunconfigured = jdbc.executeQuery("select * from cities WHERE `name`='" + params.get("cityname") + "' AND `zone`='" + params.get("zonename") + "' AND `wastetype`='" + params.get("wastetype") + "'");
                try {
                    setunconfigured.last();
                    if (setunconfigured.getRow() != 1) {
                        // TODO: 17.01.20 error handling
                    } else {
                        int id = setunconfigured.getInt("id");
                        jdbc.executeUpdate("UPDATE devices SET `CityID`='" + id + "',`DeviceName`='" + params.get("devicename") + "',`DeviceLocation`='" + params.get("devicelocation") + "' WHERE `DeviceID`='" + params.get("deviceid") + "'");
                        sb.append("{\"success\":\"true\"}");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "deleteDevice":
                try {
                    int res = jdbc.executeUpdate("DELETE FROM devices WHERE `DeviceID`='" + params.get("id") + "'");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sb.append("{\"status\":\"success\"}");
                break;
            case "getDeviceNumber":
                // TODO: 18.01.20
                break;
            case "addtodb":
                // TODO: 18.01.20

                setunconfigured = jdbc.executeQuery("select * from cities WHERE `name`='" + params.get("cityname") + "' AND `zone`='" + params.get("zonename") + "' AND `wastetype`='" + params.get("wastetype") + "'");
                try {
                    setunconfigured.last();
                    if (setunconfigured.getRow() != 1) {
                        // TODO: 17.01.20 error handling
                    } else {
                        int id = setunconfigured.getInt("id");
                        jdbc.executeUpdate("UPDATE devices SET `CityID`='" + id + "',`DeviceName`='" + params.get("devicename") + "',`DeviceLocation`='" + params.get("devicelocation") + "' WHERE `DeviceID`='" + params.get("deviceid") + "'");
                        sb.append("{\"success\":\"true\"}");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }
        return sb.toString();
    }
}
