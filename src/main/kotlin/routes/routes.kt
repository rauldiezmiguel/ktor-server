package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get

fun Application.configureRoutes() {
    routing {
        get("/") {
            call.respondText("API funcionando correctamente")
        }
        get("/hello"){
            call.respondText("¡Hola desde Ktor Backend!", ContentType.Text.Plain)
        }
    }
}