package com.wasteinformationserver.website.datarequests;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDCB;
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

        JDCB jdcb;
        try {
            jdcb = JDCB.getInstance();
        } catch (IOException e) {
            Log.error("no connection to db");
            return "{\"query\" : \"nodbconn\"}";
        }
        switch (params.get("action")) {
            case "newCity":
                sb.append("{");
                Log.debug(params.toString());

//                check if wastezone and wasteregion already exists

                Log.debug(params.get("cityname") + params.get("wastetype") + params.get("wastezone"));
                ResultSet set = jdcb.executeQuery("select * from `cities` where `name`='" + params.get("cityname") + "' AND `wastetype`='" + params.get("wastetype") + "' AND `zone`='" + params.get("wastezone") + "'");
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
                    System.out.println("doesnt exist");
                    int status = 0;
                    try {
                        status = jdcb.executeUpdate("INSERT INTO `cities`(`userid`, `name`, `wastetype`, `zone`) VALUES ('0','" + params.get("cityname") + "','" + params.get("wastetype") + "','" + params.get("wastezone") + "');");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.out.println(status);
                    if (status == 1) {
                        sb.append("\"status\" : \"inserted\"");
                    } else {
                        sb.append("\"status\" : \"inserterror\"");
                    }

                } else if (size > 1) {
                    Log.warning("more than one entry in db!!!");
                    sb.append("\"status\" : \"exists\"");
                } else {
                    //already exists
                    System.out.println("already exists");
                    sb.append("\"status\" : \"exists\"");
                }

                sb.append(",\"query\":\"ok\"");
                sb.append("}");
                break;
            case "getAllCities":
                ResultSet sett = jdcb.executeQuery("select * from cities");
                Log.debug(sett.toString());
                sb.append("{\"data\":[");
                try {
                    while (sett.next()) {
                        sb.append("{\"cityname\":\"" + sett.getString("name") + "\"");
                        sb.append(",\"wastetype\":\"" + sett.getString("wastetype") + "\"");
                        sb.append(",\"id\":\"" + sett.getString("id") + "\"");
                        sb.append(",\"zone\":\"" + sett.getString("zone") + "\"}");
                        if (!sett.isLast()) {
                            sb.append(",");
                        }

//                        System.out.println(sett.getString("name"));
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
                Log.debug(params.get("id"));
                int status = 0;
                try {
                    status = jdcb.executeUpdate("DELETE FROM `cities` WHERE `id`='" + params.get("id") + "'");
                    if (status == 1) {
                        //success
                        sb.append("\"status\" : \"success\"");
                    } else {
                        sb.append("\"status\" : \"error\"");
                    }
                } catch (SQLIntegrityConstraintViolationException e) {
                    Log.warning("dependencies of deletion exist");
                    sb.append("\"status\" : \"dependenciesnotdeleted\"");
                } catch (SQLException e) {
                    Log.error("sql exception: " + e.getMessage());
                    sb.append("\"status\" : \"error\"");
                }

                Log.debug(status);

                sb.append(",\"query\":\"ok\"");
                sb.append("}");

                break;
            case "getcollectionnumber": //todo maybe combine all three to one
                sb.append("{");

                try {
                    ResultSet settt = jdcb.executeQuery("select * from pickupdates");
                    settt.last();
                    sb.append("\"collectionnumber\":\"" + settt.getRow() + "\"");
                } catch (SQLException e) {
                    Log.error("sql exception: " + e.getMessage());
                    sb.append("\"status\" : \"error\"");
                }

                sb.append(",\"query\":\"ok\"");
                sb.append("}");
                break;
            case "getcollectioninfuture":
                sb.append("{");

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    String time = sdf.format(date);
                    ResultSet settt = jdcb.executeQuery("SELECT * FROM `pickupdates` WHERE `pickupdate` BETWEEN '"+time+"' AND '2222-12-27'");
                    settt.last();
                    sb.append("\"collectionnumber\":\"" + settt.getRow() + "\"");
                } catch (SQLException e) {
                    Log.error("sql exception: " + e.getMessage());
                    sb.append("\"status\" : \"error\"");
                }

                sb.append(",\"query\":\"ok\"");
                sb.append("}");
                break;
            case "getfinishedcollections":
                sb.append("{");

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    String time = sdf.format(date);
                    ResultSet settt = jdcb.executeQuery("SELECT * FROM `pickupdates` WHERE `pickupdate` BETWEEN  '0000-12-27' AND '"+time+"'");
                    settt.last();
                    sb.append("\"collectionnumber\":\"" + settt.getRow() + "\"");
                } catch (SQLException e) {
                    Log.error("sql exception: " + e.getMessage());
                    sb.append("\"status\" : \"error\"");
                }

                sb.append(",\"query\":\"ok\"");
                sb.append("}");
                break;
        }
        return sb.toString();
    }
}
