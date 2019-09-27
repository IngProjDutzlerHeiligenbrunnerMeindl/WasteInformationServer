package website;

import db.jdcb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class LoginRequest extends PostRequest {
    @Override
    public String request(HashMap<String, String> params) {

        String password = params.get("password");
        String username = params.get("username");

        ResultSet s = new jdcb("users", "kOpaIJUjkgb9ur6S", "wasteinformation").executeQuery("select * from user where username ='" + username + "'");

        String response = "{\"accept\": false}";
        try {
            s.last();
            if (s.getRow() == 1) {
                //success
                if (HttpTools.StringToMD5(password).equals(s.getString("password"))) {
                    System.out.println("login success");
                    LoginState.getObject().logIn();
                    LoginState.getObject().setAccountData(username,"","","");
                    response = "{\"accept\": true}";
                } else {
                    System.out.println("wrong password");
                }
            } else if (s.getRow() == 0) {
                //user not found
                System.out.println("user not found");
            } else {
                //internal error two users with same name...?
            }
            System.out.println("rowcount: " + s.getRow());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return response;
    }
}
