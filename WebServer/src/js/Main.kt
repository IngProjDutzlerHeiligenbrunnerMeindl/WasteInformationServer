import org.w3c.dom.HTMLInputElement
import org.w3c.fetch.RequestInit
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.browser.window

fun main() {
    val callurl = window.document.URL
    if(callurl.contains("sec.html")){
        document.write("Hello, in document 2!")
    }else {
        window.onload = {
            println("loaded sucessfully")
            document.getElementById("loginbtn")?.addEventListener("click",{
                it.preventDefault()
                println("clicked!!!")

                val username = document.getElementById("userfield") as HTMLInputElement
                val passfield = document.getElementById("passfield") as HTMLInputElement
                println(username.value)

                console.log("fetch 'data.json' with 'post'")
                window.fetch("/senddata/loginget", RequestInit(method = "POST", body = "username=${username.value}&password=${passfield.value}")).then {
                    it.text().then { println(it) }
                }
            })
        }
        document.write("Hello, world!")

        val req = Requester()
        req.request()
    }
}