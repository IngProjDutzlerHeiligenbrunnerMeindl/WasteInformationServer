package com.wasteinformationserver.website.datarequests;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDBC;
import com.wasteinformationserver.website.basicrequest.PostRequest;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class NewDateRequest extends PostRequest {
    @Override
    public String request(HashMap<String, String> params) {
        StringBuilder sb = new StringBuilder();
        JDBC jdbc;
        ResultSet set;
        try {
            jdbc = JDBC.getInstance();
        } catch (IOException e) {
            Log.error("no connection to db");
            return "{\"query\" : \"nodbconn\"}";
        }
        switch (params.get("action")) {
            case "getCitynames":
                set = jdbc.executeQuery("select * from cities");
                Log.debug(set.toString());
                sb.append("{\"data\":[");
                try {
                    String prev = "";
                    while (set.next()) {
                        if (prev.equals(set.getString("name"))) {

                        } else {
                            if (!set.isFirst()) {
                                sb.append(",");
                            }
                            sb.append("{\"cityname\":\"" + set.getString("name") + "\"}");
                        }
                        prev = set.getString("name");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sb.append("]");
                sb.append(",\"query\":\"ok\"");
                sb.append("}");
                Log.debug(sb.toString());
                break;
            case "getzones":
                set = jdbc.executeQuery("select * from cities WHERE `name`='" + params.get("cityname") + "' ORDER BY zone ASC");
                Log.debug(set.toString());
                sb.append("{\"data\":[");
                try {
                    int prev = 42;
                    while (set.next()) {
                        if (prev == set.getInt("zone")) {

                        } else {
                            sb.append("{\"zone\":\"" + set.getInt("zone") + "\"}");
                            if (!set.isLast()) {
                                sb.append(",");
                            }
                        }
                        prev = set.getInt("zone");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sb.append("]");
                sb.append(",\"query\":\"ok\"");
                sb.append("}");
                break;
            case "gettypes":
                set = jdbc.executeQuery("select * from cities WHERE `name`='" + params.get("cityname") + "' AND `zone`='"+params.get("zonename")+"' ORDER BY zone ASC");
                Log.debug(set.toString());
                sb.append("{\"data\":[");
                try {
                    String prev = "42";
                    while (set.next()) {
                        if (prev == set.getString("wastetype")) {

                        } else {
                            sb.append("{\"wastetype\":\"" + set.getString("wastetype") + "\"}");
                            if (!set.isLast()) {
                                sb.append(",");
                            }
                        }
                        prev = set.getString("wastetype");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sb.append("]");
                sb.append(",\"query\":\"ok\"");
                sb.append("}");
                break;
            case "newdate":
                sb.append("{");
                Log.debug(params);
                set = jdbc.executeQuery("select * from cities WHERE `name`='" + params.get("cityname") + "' AND `zone`='" + params.get("zone") + "' AND `wastetype`='" + params.get("wastetype") + "'");
                try {
                    set.last();
                    if (set.getRow() == 1) {
                        Log.debug(set.getInt("id"));

                        int status = jdbc.executeUpdate("INSERT INTO `pickupdates`(`citywastezoneid`, `pickupdate`) VALUES ('" + set.getInt("id") + "','" + params.get("date") + "')");
                        if (status == 1) {
                            sb.append("\"status\" : \"success\"");
                        } else {
                            sb.append("\"status\" : \"error\"");
                        }
                    } else {
                        Log.warning("city doesnt exist!");
                        sb.append("\"status\" : \"citydoesntexist\"");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sb.append(",\"query\":\"ok\"");
                sb.append("}");
                break;
        }
        return sb.toString();
    }
}
