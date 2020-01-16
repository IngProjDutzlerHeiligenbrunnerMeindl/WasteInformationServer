package com.wasteinformationserver.website.datarequests;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDCB;
import com.wasteinformationserver.website.basicrequest.PostRequest;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DeviceRequest extends PostRequest {
    @Override
    public String request(HashMap<String, String> params) {

        JDCB jdcb = null;
        try {
            jdcb = JDCB.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        switch (params.get("action")) {
            case "getdevices":
                ResultSet set = jdcb.executeQuery("SELECT * from devices");
                sb.append("{\"data\":[");
                try {
                    while (set.next()) {
                        int deviceid = set.getInt("DeviceID");
                        int cityid = set.getInt("CityID");
                        if (cityid == -1) {
                            //not configured
                            sb.append("{\"deviceid\":\"" + deviceid + "\",\"cityid\":\"" + cityid + "\"}");
                        } else {
                            String devicename = set.getString("DeviceName");
                            String devicelocation = set.getString("DeviceLocation");

                            sb.append("{\"deviceid\":\"" + deviceid + "\",\"cityid\":\"" + cityid + "\",\"devicename\":\"" + devicename + "\",\"devicelocation\":\"" + devicelocation + "\"}");
                        }

                        if (!set.isLast()) {
                            sb.append(",");
                        }
                    }
                    sb.append("]}");
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                break;
            case "getCitynames":
                set = jdcb.executeQuery("select * from cities");
                Log.debug(set.toString());
                sb.append("{");
                try {
                    String prev = "";
                    while (set.next()) {
                        if (prev.equals(set.getString("name"))) {

                        } else {
                            if (!set.isFirst()) {
                                sb.append(",");
                            }
                            sb.append("\"" + set.getString("name") + "\":\"" + set.getString("name") + "\"");
                        }
                        prev = set.getString("name");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sb.append("}");
                Log.debug(sb.toString());
                break;
            case "getzones":
                set = jdcb.executeQuery("select * from cities WHERE `name`='" + params.get("cityname") + "' ORDER BY zone ASC");
                Log.debug(set.toString());
                sb.append("{");
                try {
                    int prev = 42;
                    while (set.next()) {
                        if (prev == set.getInt("zone")) {

                        } else {
                            sb.append("\"" + set.getInt("zone") + "\":\"" + set.getInt("zone") + "\"");
                            if (!set.isLast()) {
                                sb.append(",");
                            }
                        }
                        prev = set.getInt("zone");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sb.append("}");
                break;
            case "gettypes":
                set = jdcb.executeQuery("select * from cities WHERE `name`='" + params.get("cityname") + "' AND `zone`='" + params.get("zonename") + "' ORDER BY zone ASC");
                Log.debug(set.toString());
                sb.append("{");
                try {
                    String prev = "42";
                    while (set.next()) {
                        if (prev == set.getString("wastetype")) {

                        } else {
                            sb.append("\"" + set.getString("wastetype") + "\":\"" + set.getString("wastetype") + "\"");
                            if (!set.isLast()) {
                                sb.append(",");
                            }
                        }
                        prev = set.getString("wastetype");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sb.append("}");
                break;
            case "savetodb":
                set = jdcb.executeQuery("select * from cities WHERE `name`='" + params.get("cityname") + "' AND `zone`='" + params.get("zonename") + "' AND `wastetype`='" + params.get("wastetype") + "'");
                try {
                    set.last();
                    if (set.getRow() != 1) {
                        //error
                    } else {
                        int id = set.getInt("id");
                        jdcb.executeUpdate("UPDATE devices SET `CityID`='" + id + "',`DeviceName`='" + params.get("devicename") + "',`DeviceLocation`='" + params.get("devicelocation") + "' WHERE `DeviceID`='" + params.get("deviceid") + "'");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }


                //'action=savetodb&cityname=' + cityname + '&zonename=' + zone+'&wastetype='+wastetype+'&devicename='+devicename+'&devicelocation='+devicelocation
                break;
        }
        return sb.toString();
    }
}
