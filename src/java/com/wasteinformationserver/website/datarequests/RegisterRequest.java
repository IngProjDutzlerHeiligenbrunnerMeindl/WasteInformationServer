package com.wasteinformationserver.website.datarequests;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDCB;
import com.wasteinformationserver.website.HttpTools;
import com.wasteinformationserver.website.basicrequest.PostRequest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class RegisterRequest extends PostRequest {
    @Override
    public String request(HashMap<String, String> params) {
        Log.debug(params.toString());

        String passhash = HttpTools.StringToMD5(params.get("password"));

        JDCB myjd = null;
        try {
            myjd = JDCB.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //new JDCB("users", "kOpaIJUjkgb9ur6S", "wasteinformation");
        try {
            int s = myjd.executeUpdate("INSERT INTO `user` (`username`, `firstName`, `secondName`, `password`, `email`, `logindate`) VALUES ('"+params.get("username")+"', '"+params.get("firstname")+"', '"+params.get("lastname")+"', '"+passhash+"', '"+params.get("email")+"', current_timestamp());");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // TODO: 27.09.19 detect if register process was successful and reply right json
        return "{\"accept\": true}";
    }
}
