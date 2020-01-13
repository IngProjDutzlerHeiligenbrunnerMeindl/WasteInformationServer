package com.wasteinformationserver.website.datarequests;

import com.wasteinformationserver.db.JDCB;
import com.wasteinformationserver.website.basicrequest.PostRequest;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DeviceRequest extends PostRequest {
    @Override
    public String request(HashMap<String, String> params) {

        JDCB jdcb = null;
        try {
            jdcb = JDCB.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (params.get("action")) {
            case "getdevices":
                ResultSet set = jdcb.executeQuery("SELECT * from devices");
                StringBuilder sb = new StringBuilder("{\"data\":[");
                try {
                    while (set.next()) {
                        sb.append("{\"name\":\"" + set.getString("devicename") + "\"}");
                        if (!set.isLast()) {
                            sb.append(",");
                        }
                    }
                    sb.append("]}");
                    return sb.toString();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                break;
        }
        return null;
    }
}
