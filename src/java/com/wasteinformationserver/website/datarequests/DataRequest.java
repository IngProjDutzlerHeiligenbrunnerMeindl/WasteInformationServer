package com.wasteinformationserver.website.datarequests;

import com.wasteinformationserver.basicutils.Info;
import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDBC;
import com.wasteinformationserver.website.basicrequest.PostRequest;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DataRequest extends PostRequest {
    @Override
    public String request(HashMap<String, String> params) {
        StringBuilder sb = new StringBuilder();
        ResultSet set = null;
        int status = -1;

        JDBC jdbc;
        try {
            jdbc = JDBC.getInstance();
        } catch (IOException e) {
            Log.Log.error("no connection to db");
            return "{\"query\" : \"nodbconn\"}";
        }
        switch (params.get("action")) {
            case "newCity":
                sb.append("{");
                Log.Log.debug(params.toString());

//                check if wastezone and wasteregion already exists

                Log.Log.debug(params.get("cityname") + params.get("wastetype") + params.get("wastezone"));
                set = jdbc.executeQuery("select * from `cities` where `name`='" + params.get("cityname") + "' AND `wastetype`='" + params.get("wastetype") + "' AND `zone`='" + params.get("wastezone") + "'");
                int size = 0;
                try {
                    if (set != null) {
                        set.last();    // moves cursor to the last row
                        size = set.getRow(); // get row id
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (size == 0) {
                    //doesnt exist
                    try {
                        status = jdbc.executeUpdate("INSERT INTO `cities`(`userid`, `name`, `wastetype`, `zone`) VALUES ('0','" + params.get("cityname") + "','" + params.get("wastetype") + "','" + params.get("wastezone") + "');");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    if (status == 1) {
                        sb.append("\"status\" : \"inserted\"");
                    } else {
                        sb.append("\"status\" : \"inserterror\"");
                    }

                } else if (size > 1) {
                    Log.Log.warning("more than one entry in db!!!");
                    sb.append("\"status\" : \"exists\"");
                } else {
                    //already exists
                    sb.append("\"status\" : \"exists\"");
                }

                sb.append(",\"query\":\"ok\"");
                sb.append("}");
                break;
            case "getAllCities":
                set = jdbc.executeQuery("select * from cities");
                Log.Log.debug(set.toString());
                sb.append("{\"data\":[");
                try {
                    while (set.next()) {
                        sb.append("{\"cityname\":\"" + set.getString("name") + "\"");
                        sb.append(",\"wastetype\":\"" + set.getString("wastetype") + "\"");
                        sb.append(",\"id\":\"" + set.getString("id") + "\"");
                        sb.append(",\"zone\":\"" + set.getString("zone") + "\"}");
                        if (!set.isLast()) {
                            sb.append(",");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sb.append("]");
                sb.append(",\"query\":\"ok\"");
                sb.append("}");
                break;
            case "deletecity":
                //DELETE FROM `cities` WHERE `id`=0
                sb.append("{");
                try {
                    status = jdbc.executeUpdate("DELETE FROM `cities` WHERE `id`='" + params.get("id") + "'");
                    if (status == 1) {
                        //success
                        sb.append("\"status\" : \"success\"");
                    } else {
                        sb.append("\"status\" : \"error\"");
                    }
                } catch (SQLIntegrityConstraintViolationException e) {
                    Log.Log.warning("dependencies of deletion exist");
                    sb.append("\"status\" : \"dependenciesnotdeleted\"");
                } catch (SQLException e) {
                    Log.Log.error("sql exception: " + e.getMessage());
                    sb.append("\"status\" : \"error\"");
                }

                Log.Log.debug(status);

                sb.append(",\"query\":\"ok\"");
                sb.append("}");

                break;
            case "getAllDates":
                set = jdbc.executeQuery("SELECT pickupdates.id,pickupdates.pickupdate,cities.userid,cities.name,cities.wastetype,cities.zone " +
                        "FROM `pickupdates` INNER JOIN `cities` ON pickupdates.citywastezoneid = cities.id");
                sb.append("{\"data\":[");
                try {
                    while (set.next()) {
                        sb.append("{\"date\":\"" + set.getString("pickupdate") + "\"");
                        sb.append(",\"cityname\":\"" + set.getString("name") + "\"");
                        sb.append(",\"wastetype\":\"" + set.getString("wastetype") + "\"");
                        sb.append(",\"id\":\"" + set.getString("id") + "\"");
                        sb.append(",\"zone\":\"" + set.getString("zone") + "\"}");
                        if (!set.isLast()) {
                            sb.append(",");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sb.append("]");
                sb.append(",\"query\":\"ok\"");
                sb.append("}");
                break;
            case "deletedate":
                sb.append("{");
                try {
                    status = jdbc.executeUpdate("DELETE FROM `pickupdates` WHERE `id`='" + params.get("id") + "'");
                    if (status == 1) {
                        //success
                        sb.append("\"status\" : \"success\"");
                    } else {
                        sb.append("\"status\" : \"error\"");
                    }
                } catch (SQLIntegrityConstraintViolationException e) {
                    Log.Log.warning("dependencies of deletion exist");
                    sb.append("\"status\" : \"dependenciesnotdeleted\"");
                } catch (SQLException e) {
                    Log.Log.error("sql exception: " + e.getMessage());
                    sb.append("\"status\" : \"error\"");
                }

                sb.append(",\"query\":\"ok\"");
                sb.append("}");
                break;
            case "getversionandbuildtime":
                sb.append("{");

                sb.append("\"version\" : \""+ Info.getVersion()+"\"");
                sb.append(",\"buildtime\" : \""+ Info.getBuilddate()+"\"");


                sb.append(",\"query\":\"ok\"");
                sb.append("}");
                break;
            case "getStartHeaderData":
                sb.append("{");

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    String time = sdf.format(date);
                    set = jdbc.executeQuery("SELECT * FROM `pickupdates` WHERE `pickupdate` BETWEEN  '0000-12-27' AND '"+time+"'");
                    set.last();
                    sb.append("\"finshedcollections\":\"" + set.getRow() + "\"");

                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    date = new Date();
                    date = new Date(date.getTime()+1 * 24 * 60 * 60 * 1000);

                    time = sdf.format(date);
                    set = jdbc.executeQuery("SELECT * FROM `pickupdates` WHERE `pickupdate` BETWEEN '"+time+"' AND '2222-12-27'");
                    set.last();
                    sb.append(",\"futurecollections\":\"" + set.getRow() + "\"");

                    set = jdbc.executeQuery("select * from pickupdates");
                    set.last();
                    sb.append(",\"collectionnumber\":\"" + set.getRow() + "\"");

                    set = jdbc.executeQuery("select * from `cities`");
                    set.last();
                    sb.append(",\"citynumber\":\"" + set.getRow() + "\"");
                } catch (SQLException e) {
                    Log.Log.error("sql exception: " + e.getMessage());
                    sb.append("\"status\" : \"error\"");
                }

                sb.append(",\"query\":\"ok\"");
                sb.append("}");
                break;
        }
        return sb.toString();
    }
}
