package application

import authentication.AuthConfig.configureAuthentication
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import org.postgresql.gss.MakeGSS.authenticate
import routes.*

//import routing.configureRouting

fun main(args: Array<String>) {
    val port = System.getenv("PORT")?.toInt() ?: 8080

    embeddedServer(Netty, host = "0.0.0.0", port = port, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    DatabaseFactory.init()
    configureAuthentication()

    configureRoutes()
    authRoutes()
    userRoutes()
    clubRoutes()
    equipoRoutes()
    entrenadorEquipoRoutes()
    entrenamientoRoutes()
    partidoRoutes()
    jugadorRoutes()
    evaluacionesRoutes()
    estadisticasJugadorRoutes()
    estadisticasPartidoRoutes()
    estadisticasEquipoRoutes()
    estadisticasTotalJugadorRoutes()
    temporadasRoutes()
    asistenciaEntrenosRoutes()
    fichaJugadorRoutes()
}
