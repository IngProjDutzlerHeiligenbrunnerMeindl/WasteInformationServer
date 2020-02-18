import kotlin.browser.window

fun main() {
    val callurl = window.document.URL
    window.onload = {
        if (callurl.endsWith("/") || callurl.contains("index.html")) {
            println("loaded sucessfully")
            Index()
        }
        else if (callurl.contains("dashboard.html")) {
            Dashboard()
        }
        else {
            println("js called from undefined html file")
        }
    }

}