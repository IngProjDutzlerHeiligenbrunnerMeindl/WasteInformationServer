package com.wasteinformationserver.website

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.wasteinformationserver.basicutils.Log.Log.debug
import com.wasteinformationserver.basicutils.Log.Log.warning
import com.wasteinformationserver.website.datarequests.login.LoginState
import java.io.IOException

class MainPage : HttpHandler {
    @Throws(IOException::class)
    override fun handle(t: HttpExchange) {
        var path = t.requestURI.path
        if (path == "/") {
            path += "index.html"
        }
        debug("looking for: $path")
        if (path.contains(".html")) {
            if (LoginState.getObject().isLoggedIn || path == "/register.html" || path == "/index.html") { //pass only register page
                sendPage(path, t)
            } else {
                warning("user not logged in --> redirecting to login page")
                sendPage("/index.html", t)
            }
        } else { //only detect login state on html pages
            sendPage(path, t)
        }
    }

    @Throws(IOException::class)
    private fun sendPage(path: String, t: HttpExchange) {
        val fs = javaClass.getResourceAsStream("/wwwroot$path")
        if (fs == null && path.contains(".html")) {
            warning("wrong page sending 404")
            sendPage("/404Error.html", t)
        } else if (fs == null) {
            warning("requested resource doesnt exist --> $path")
        } else { // Object exists and is a file: accept with response code 200.
            var mime = "text/html"
            val s = path.substring(path.length - 3)
            if (s == ".js") mime = "application/javascript"
            if (s == "css") mime = "text/css"
            val h = t.responseHeaders
            h["Content-Type"] = mime
            t.sendResponseHeaders(200, 0)
            val os = t.responseBody
            val buffer = ByteArray(0x10000)
            var count: Int
            while (fs.read(buffer).also { count = it } >= 0) {
                os.write(buffer, 0, count)
            }
            fs.close()
            os.close()
        }
    }
}