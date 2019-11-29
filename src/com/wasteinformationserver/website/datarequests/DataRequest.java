package com.wasteinformationserver.website.datarequests;

import com.google.gson.Gson;
import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDCB;
import com.wasteinformationserver.website.basicrequest.PostRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DataRequest extends PostRequest {
    @Override
    public String request(HashMap<String, String> params) {
        String result = "";
        switch (params.get("action")) {
            case "newCity":
                Log.debug(params.toString());

//                check if wastezone and wasteregion already exists

                JDCB jdcb = new JDCB("users", "kOpaIJUjkgb9ur6S", "wasteinformation");

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
                    int status = jdcb.executeUpdate("INSERT INTO `cities`(`userid`, `name`, `wastetype`, `zone`) VALUES ('0','" + params.get("cityname") + "','" + params.get("wastetype") + "','" + params.get("wastezone") + "');");
                    System.out.println(status);
                    if (status == 1) {
                        result = "{\"status\" : \"inserted\"}";
                    } else {
                        result = "{\"status\" : \"inserterror\"}";
                    }

                } else if (size > 1) {
                    Log.warning("more than one entry in db!!!");
                    result = "{\"status\" : \"exists\"}";
                } else {
                    //already exists
                    System.out.println("already exists");
                    result = "{\"status\" : \"exists\"}";
                }

                Log.debug(result);
                break;
            case "getAllCities":

                // TODO: 15.11.19 database call to get all data and store it as json.
                JDCB jdcbc = new JDCB("users", "kOpaIJUjkgb9ur6S", "wasteinformation");
                Gson gson = new Gson();

                StringBuilder builder = new StringBuilder();

                ResultSet sett = jdcbc.executeQuery("select * from cities");
                Log.debug(sett.toString());
                builder.append("{\"data\":[");
                try {
                    while (sett.next()) {
                        builder.append("{\"cityname\":\"" + sett.getString("name") + "\"");
                        builder.append(",\"wastetype\":\"" + sett.getString("wastetype") + "\"");
                        builder.append(",\"id\":\"" + sett.getString("id") + "\"");
                        builder.append(",\"zone\":\"" + sett.getString("zone") + "\"}");
                        if (!sett.isLast()) {
                            builder.append(",");
                        }

//                        System.out.println(sett.getString("name"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                builder.append("]}");
                result = builder.toString();
                Log.debug(result);
                break;
            case "deletecity":
                //DELETE FROM `cities` WHERE `id`=0

                JDCB jdcbcc = new JDCB("users", "kOpaIJUjkgb9ur6S", "wasteinformation");

                Log.debug(params.get("id"));
                int status= jdcbcc.executeUpdate("DELETE FROM `cities` WHERE `id`='" + params.get("id")+"'");
                Log.debug(status);

                break;
        }
        return result;
    }
}
