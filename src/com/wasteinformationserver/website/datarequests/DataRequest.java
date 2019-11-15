package com.wasteinformationserver.website.datarequests;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDCB;
import com.wasteinformationserver.website.basicrequest.PostRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DataRequest extends PostRequest {
    @Override
    public String request(HashMap<String, String> params) {
        switch (params.get("action")) {
            case "newCity":
                Log.debug(params.toString());

//                check if wastezone and wasteregion already exists

                JDCB jdcb = new JDCB("users", "kOpaIJUjkgb9ur6S", "wasteinformation");

                ResultSet set = jdcb.executeQuery("select * from cities where name='" + params.get("cityname") + "' AND wastetype='" + params.get("wastetype") + "' AND zone='" + params.get("wastezone") + "'");
                try {
                    if (set.getFetchSize() == 0) {
                        //doesnt exist
                        System.out.println("doesnt exist");
                        jdcb.executeUpdate("INSERT INTO `cities`(`userid`, `name`, `wastetype`, `zone`) VALUES ('0','" + params.get("cityname") + "','" + params.get("wastetype") + "','" + params.get("wastezone") + "');");
                    } else {
                        //already exists
                        System.out.println("already exists");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "getAllCities":

                // TODO: 15.11.19 database call to get all data and store it as json. 
                
                break;
        }
        return "";
    }
}
