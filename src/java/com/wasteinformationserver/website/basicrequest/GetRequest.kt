package com.wasteinformationserver.website.basicrequest

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import java.io.IOException
import java.util.*

/**
 * basic GET request handler
 * reply function has to be implemented!
 */
abstract class GetRequest : HttpHandler {
    @Throws(IOException::class)
    override fun handle(httpExchange: HttpExchange) {
        if (httpExchange.requestMethod == "GET") {
            val query = httpExchange.requestURI.query
            val params = HashMap<String, String>()
            val res = query.split("&".toRegex()).toTypedArray()
            for (str in res) {
                val values = str.split("=".toRegex()).toTypedArray()
                params[values[0]] = values[1]
            }
            val response = myrequest(params)
            val h = httpExchange.responseHeaders
            h["Content-Type"] = "application/json"
            httpExchange.sendResponseHeaders(200, 0)
            val os = httpExchange.responseBody
            os.write(response.toByteArray())
            os.close()
        }
    }

    /**
     * @param params received get params from com.wasteinformationserver.website
     * @return json reply to com.wasteinformationserver.website
     */
    abstract fun myrequest(params: HashMap<String, String>): String
}