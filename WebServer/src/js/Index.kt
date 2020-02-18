import org.w3c.dom.HTMLInputElement
import org.w3c.fetch.RequestInit
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Json

class Index {
    init {
        document.getElementById("loginbtn")?.addEventListener("click", {
            it.preventDefault()
            println("clicked!!!")

            val username = document.getElementById("userfield") as HTMLInputElement
            val passfield = document.getElementById("passfield") as HTMLInputElement
            println(username.value)

            console.log("fetch 'data.json' with 'post'")
            window.fetch("/senddata/loginget", RequestInit(method = "POST", body = "username=${username.value}&password=${passfield.value}")).then { response ->
                response.text().then { text ->
                    val json = JSON.parse<Json>(text)
                    if (json["status"] == "nodbconn") {
                        js("""Swal.fire({
                            type: "error",
                            title: 'No connection to Database',
                            html: 'Setup DB here --> <a href="index.html">click<a/>.'
                        })""")
                    }

                    if (json["accept"] == true) {
                        println("successfully logged in!")
                        document.cookie = "username=$username"
                        window.location.replace("dashboard.html")
                    }
                }
            }
        })
    }
    // todo register pwa correctly
}