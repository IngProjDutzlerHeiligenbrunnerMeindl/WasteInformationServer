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
            jdbc = JDBC.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        switch (params.get("action")) {
            case "getdevices":
                ResultSet deviceset = jdbc.executeQuery("SELECT * FROM `devices");

                sb.append("{\"data\":[");
                try {
                    while (deviceset.next()) {
                        int deviceid = deviceset.getInt("DeviceID");
                        int cityid = deviceset.getInt("CityID");

                        if (cityid == -1) {
                            sb.append("{\"deviceid\":\"").append(deviceid).append("\",\"cityid\":\"").append(cityid).append("\"}");
                        } else {
                            String devicename = deviceset.getString("DeviceName");
                            String devicelocation = deviceset.getString("DeviceLocation");

                            sb.append("{\"deviceid\":\"").append(deviceid).append("\",\"devicename\":\"").append(devicename).append("\",\"devicelocation\":\"").append(devicelocation).append("\",\"devices\":[");

                            ResultSet devicecities = jdbc.executeQuery("SELECT * FROM `device_city` INNER JOIN `cities` ON device_city.CityID=cities.id WHERE `DeviceID`='" + deviceid + "'");
                            while (devicecities.next()) {
                                int cityidd = devicecities.getInt("id");
                                String cityname = devicecities.getString("name");
                                String wastetype = devicecities.getString("wastetype");
                                String zone = devicecities.getString("zone");

                                sb.append("{\"cityid\":\"").append(cityidd).append("\",\"cityname\":\"").append(cityname).append("\",\"wastetype\":\"").append(wastetype).append("\",\"zone\":\"").append(zone).append("\"}");
                                if (!(devicecities.isLast())) {
                                    sb.append(",");
                                }
                            }
                            sb.append("]}");
                        }
                        if (!(deviceset.isLast())) {
                            sb.append(",");
                        }
                    }
                    sb.append("]}");
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                break;
            case "getCitynames":
                deviceset = jdbc.executeQuery("select * from cities");
                Log.debug(deviceset.toString());
                sb.append("{");
                try {
                    String prev = "";
                    while (deviceset.next()) {
                        if (!prev.equals(deviceset.getString("name"))) {
                            if (!deviceset.isFirst()) {
                                sb.append(",");
                            }
                            sb.append("\"").append(deviceset.getString("name")).append("\":\"").append(deviceset.getString("name")).append("\"");
                        }
                        prev = deviceset.getString("name");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sb.append("}");
                Log.debug(sb.toString());
                break;
            case "getzones":
                deviceset = jdbc.executeQuery("select * from cities WHERE `name`='" + params.get("cityname") + "' ORDER BY zone ASC");
                Log.debug(deviceset.toString());
                sb.append("{");
                try {
                    int prev = 42;
                    while (deviceset.next()) {
                        if (prev != deviceset.getInt("zone")) {
                            sb.append("\"").append(deviceset.getInt("zone")).append("\":\"").append(deviceset.getInt("zone")).append("\"");
                            if (!deviceset.isLast()) {
                                sb.append(",");
                            }
                        }
                        prev = deviceset.getInt("zone");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sb.append("}");
                break;
            case "gettypes":
                deviceset = jdbc.executeQuery("select * from cities WHERE `name`='" + params.get("cityname") + "' AND `zone`='" + params.get("zonename") + "' ORDER BY zone ASC");
                Log.debug(deviceset.toString());
                sb.append("{");
                try {
                    String prev = "42";
                    while (deviceset.next()) {
                        if (!prev.equals(deviceset.getString("wastetype"))) {
                            sb.append("\"" + deviceset.getString("wastetype") + "\":\"" + deviceset.getString("wastetype") + "\"");
                            if (!deviceset.isLast()) {
                                sb.append(",");
                            }
                        }
                        prev = deviceset.getString("wastetype");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sb.append("}");
                break;
            case "savetodb":
                try {
                    ResultSet cityset = jdbc.executeQuery("SELECT id from cities WHERE `name`='" + params.get("cityname") + "' AND `zone`='" + params.get("zonename") + "' AND `wastetype`='" + params.get("wastetype") + "'");
                    cityset.last();
                    if (cityset.getRow() != 1) {
                        // TODO: 17.01.20 error handling
                    } else {
                        int cityid = cityset.getInt("id");

                        jdbc.executeUpdate("INSERT INTO `device_city` (`DeviceID`, `CityID`) VALUES ('" + params.get("deviceid") + "', '" + cityid + "');");
                        jdbc.executeUpdate("UPDATE devices SET `CityID`='0',`DeviceName`='" + params.get("devicename") + "',`DeviceLocation`='" + params.get("devicelocation") + "' WHERE `DeviceID`='" + params.get("deviceid") + "'");
                        sb.append("{\"success\":\"true\"}");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "deleteDevice":
                try {
                    jdbc.executeUpdate("DELETE FROM devices WHERE `DeviceID`='" + params.get("id") + "'");
                    jdbc.executeUpdate("DELETE FROM device_city WHERE `DeviceID`='" + params.get("id") + "'");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sb.append("{\"status\":\"success\"}");
                break;
            case "getDeviceNumber":
                try {
                    ResultSet numberset = jdbc.executeQuery("SELECT * FROM devices");
                    numberset.last();
                    int devicenr = numberset.getRow();

                    sb.append("{\"devicenr\":\"" + devicenr + "\"}");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "addtodb":
                int cityid = -1;
                try {
                    ResultSet device = jdbc.executeQuery("SELECT * FROM cities WHERE name='" + params.get("cityname") + "' AND wastetype='" + params.get("wastetype") + "' AND zone='" + params.get("zonename") + "'");
                    device.first();
                    cityid = device.getInt("id");
                    jdbc.executeUpdate("INSERT INTO `device_city` (`DeviceID`, `CityID`) VALUES ('" + params.get("deviceid") + "', '" + cityid + "');");
                } catch (SQLException e) {
                    e.printStackTrace();
                }


                sb.append("{\"success\":true}");
                break;
        }
        return sb.toString();
    }
}
