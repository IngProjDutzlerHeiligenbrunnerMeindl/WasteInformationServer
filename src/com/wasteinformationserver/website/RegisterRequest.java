package com.wasteinformationserver.website;

import com.wasteinformationserver.db.PostRequest;
import com.wasteinformationserver.db.jdcb;

import java.util.HashMap;

public class RegisterRequest extends PostRequest {
    @Override
    public String request(HashMap<String, String> params) {
        System.out.println(params.toString());

        String passhash = HttpTools.StringToMD5(params.get("password"));

        jdcb myjd = new jdcb("users", "kOpaIJUjkgb9ur6S", "wasteinformation");
        int s = myjd.executeUpdate("INSERT INTO `user` (`username`, `firstName`, `secondName`, `password`, `email`, `logindate`) VALUES ('"+params.get("username")+"', '"+params.get("firstname")+"', '"+params.get("lastname")+"', '"+passhash+"', '"+params.get("email")+"', current_timestamp());");

        // TODO: 27.09.19 detect if register process was successful and reply right json
        return "{\"accept\": true}";
    }
}
