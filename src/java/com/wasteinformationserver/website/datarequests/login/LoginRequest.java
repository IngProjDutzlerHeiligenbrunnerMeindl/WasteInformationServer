package com.wasteinformationserver.website.datarequests.login;

import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.db.JDBC;
import com.wasteinformationserver.website.HttpTools;
import com.wasteinformationserver.website.basicrequest.PostRequest;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * request handler of new login request of user
 * - checks the truth of username and password
 * - replies right error messages or the success login
 */
public class LoginRequest extends PostRequest {
    @Override
    public String request(HashMap<String, String> params) {

        Log.Log.message("new login request");

        String password = params.get("password");
        String username = params.get("username");

        JDBC jdbc;
        try {
            jdbc = JDBC.getInstance();
        } catch (IOException e) {
            Log.Log.error("no connection to db");
            return "{\"status\" : \"nodbconn\"}";
        }

        ResultSet s = jdbc.executeQuery("select * from user where username ='" + username + "'");

        //new JDCB("users", "kOpaIJUjkgb9ur6S", "wasteinformation").executeQuery("select * from user where username ='" + username + "'");
        Log.Log.debug("successfully logged in to db");
        String response = "{\"accept\": false}";
        try {
            s.last();
            if (s.getRow() == 1) {
                //success
                if (HttpTools.Companion.StringToMD5(password).equals(s.getString("password"))) {
                    Log.Log.debug("login success");
                    LoginState.getObject().logIn();
                    LoginState.getObject().setAccountData(username, "", "", "", s.getInt("permission")); // TODO: 06.12.19
                    response = "{\"accept\": true}";
                } else {
                    Log.Log.debug("wrong password");
                }
            } else if (s.getRow() == 0) {
                //user not found
                Log.Log.debug("user not found");
            } else {
                //internal error two users with same name...?
            }
            Log.Log.debug("rowcount: " + s.getRow());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return response;
    }
}
