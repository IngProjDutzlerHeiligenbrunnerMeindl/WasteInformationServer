package website;

import java.util.HashMap;

public class CheckLoginState extends PostRequest {
    @Override
    public String request(HashMap<String, String> params) {
        System.out.println("checkin login state");
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
