package com.wasteinformationserver.website;

import com.wasteinformationserver.basicutils.Log;

import java.util.HashMap;

public class CheckLoginState extends PostRequest {
    @Override
    public String request(HashMap<String, String> params) {
        Log.message("checking login state");
        if ((params.get("action")).equals("getloginstate")){
            if (LoginState.getObject().isLoggedIn()){
                return "{\"loggedin\":true, \"username\":\""+LoginState.getObject().getUsername()+"\"}";
            }else {
                return "{\"loggedin\":false}";
            }
        }else if ((params.get("action")).equals("logout")){
            System.out.println("logging out");
            LoginState.getObject().logOut();
            return "{\"loggedin\":false}";
        }
        return "{\"loggedin\":false}";
    }
}
