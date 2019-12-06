package com.wasteinformationserver.website.datarequests;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDCB;
import com.wasteinformationserver.website.basicrequest.PostRequest;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class NewDateRequest extends PostRequest {
    @Override
    public String request(HashMap<String, String> params) {
        StringBuilder sb = new StringBuilder();
        JDCB jdcb;
        try {
            jdcb = JDCB.getInstance();
        } catch (IOException e) {
            Log.error("no connection to db");
            return "{\"query\" : \"nodbconn\"}";
        }
        switch (params.get("action")) {
            case "getCitynames":
                ResultSet sett = jdcb.executeQuery("select * from cities");
                Log.debug(sett.toString());
                sb.append("{\"data\":[");
                try {
                    String prev = "";
                    while (sett.next()) {
                        if (prev.equals(sett.getString("name"))) {

                        } else {
                            if (!sett.isFirst()) {
                                sb.append(",");
                            }
                            sb.append("{\"cityname\":\"" + sett.getString("name") + "\"}");
                        }
                        prev = sett.getString("name");
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
                ResultSet set = jdcb.executeQuery("select * from cities WHERE `name`='" + params.get("cityname") + "' ORDER BY zone ASC");
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
            case "newdate":
                sb.append("{");
                Log.debug(params);
                ResultSet seti = jdcb.executeQuery("select * from cities WHERE `name`='" + params.get("cityname") + "' AND `zone`='" + params.get("zone") + "' AND `wastetype`='" + params.get("wastetype") + "'");
                try {
                    seti.last();
                    if (seti.getRow() == 1) {
                        Log.debug(seti.getInt("id"));

                        int status = jdcb.executeUpdate("INSERT INTO `pickupdates`(`citywastezoneid`, `pickupdate`) VALUES ('" + seti.getInt("id") + "','" + params.get("date") + "')");
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
