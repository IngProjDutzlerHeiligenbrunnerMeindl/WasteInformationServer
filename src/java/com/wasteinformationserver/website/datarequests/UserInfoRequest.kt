package com.wasteinformationserver.website.datarequests

import com.wasteinformationserver.website.basicrequest.PostRequest
import java.util.*

class UserInfoRequest : PostRequest() {
    override fun request(params: HashMap<String, String>): String {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        when (params["action"]) {
            "getlogins" -> {
                println("heyho")
            }
            "" -> {
                //todo o
            }
        }
        return "{}"
    }
}