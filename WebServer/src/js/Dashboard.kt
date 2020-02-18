import org.w3c.fetch.RequestInit
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Json

class Dashboard {
    init {
        window.fetch("/senddata/wastedata", RequestInit(method = "POST", body = "action=getStartHeaderData")).then { it -> it.text().then {
            println("response text is: "+it)
            val json = JSON.parse<Json>(it)
            document.getElementById("total-connection-labels")?.innerHTML  = json["collectionnumber"] as String
            document.getElementById("planed-collection-label")?.innerHTML  = json["futurecollections"] as String

            document.getElementById("finished-collection-label")?.innerHTML   = json["finshedcollections"] as String

            document.getElementById("total-city-number-label")?.innerHTML   = json["citynumber"] as String
        } }
    }
}