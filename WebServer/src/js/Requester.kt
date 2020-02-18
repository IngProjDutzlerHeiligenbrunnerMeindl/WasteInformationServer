import org.w3c.xhr.XMLHttpRequest

class Requester {
    fun request(){
        val test = XMLHttpRequest()

        test.open("GET","https://api.ipify.org?format=json")

        test.onload = {
            println(test.responseText)
        }

        test.send()
    }
}