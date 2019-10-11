package com.wasteinformationserver.website;

import com.wasteinformationserver.basicutils.Log;

import java.util.HashMap;

public class DataRequest extends PostRequest {
    @Override
    public String request(HashMap<String, String> params) {
        switch (params.get("action")){
            case "senddata":
                Log.debug(params.toString());

                // TODO: 11.10.19 store data in database
                break;
        }
        return "";
    }
}
