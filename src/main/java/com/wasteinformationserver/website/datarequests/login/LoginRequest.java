package com.wasteinformationserver.website.datarequests.login;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDCB;
import com.wasteinformationserver.website.HttpTools;
import com.wasteinformationserver.website.basicrequest.PostRequest;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class LoginRequest extends PostRequest {
    @Override
    public String request(HashMap<String, String> params) {

        Log.message("new login request");

        String password = params.get("password");
        String username = params.get("username");

        JDCB jdcb;
        try {
            jdcb = JDCB.getInstance();
        } catch (IOException e) {
            Log.error("no connection to db");
            return "{\"status\" : \"nodbconn\"}";
        }

        ResultSet s = jdcb.executeQuery("select * from user where username ='" + username + "'");;
        //new JDCB("users", "kOpaIJUjkgb9ur6S", "wasteinformation").executeQuery("select * from user where username ='" + username + "'");
        Log.debug("successfully logged in to db");
        String response = "{\"accept\": false}";
        try {
            s.last();
            if (s.getRow() == 1) {
                //success
                if (HttpTools.StringToMD5(password).equals(s.getString("password"))) {
                    Log.debug("login success");
                    LoginState.getObject().logIn();
                    LoginState.getObject().setAccountData(username,"","","");
                    response = "{\"accept\": true}";
                } else {
                    Log.debug("wrong password");
                }
            } else if (s.getRow() == 0) {
                //user not found
                Log.debug("user not found");
            } else {
                //internal error two users with same name...?
            }
            Log.debug("rowcount: " + s.getRow());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return response;
    }
}
