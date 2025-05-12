package routes

import authentication.AuthConfig
import authentication.PasswordHasher
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.UsuarioAuthResponse
import model.UsuarioDTO
import services.UserService

fun Application.authRoutes() {
    val userService = UserService()

    routing {
        route("/auth") {
            post("/register") {
                val request = call.receive<Map<String, String>>()
                val nombreUsuario = request["nombreUsuario"] ?: return@post call.respond("Falta nombre de usuario")
                val passWrd = request["passWrd"] ?: return@post call.respond("Falta la contraseña")
                val tipoUsuario = request["tipoUsuario"] ?: return@post call.respond("Falta tipo de usuario")
                val idClub = request["idClub"]?.toIntOrNull()

                val hashedPassword = PasswordHasher.hashPassword(passWrd)

                val user = userService.createUser(nombreUsuario, hashedPassword, tipoUsuario, idClub)
                if (user != null) {
                    call.respond("Usuario creado con ID: ${user.id.value}")
                }
            }

            post("/login") {
                val request = call.receive<Map<String, String>>()
                val nombreUsuario = request["nombreUsuario"] ?: return@post call.respond("Falta nombre de usuario")
                val passWrd = request["passWrd"] ?: return@post call.respond("Falta la contraseña")

                val user = userService.authenticateUser(nombreUsuario, passWrd)
                if (user != null) {
                    // Generar el Access Token y Refresh Token
                    val accessToken = AuthConfig.generateToken(user.nombreUsuario)
                    val refreshToken = AuthConfig.generateRefreshToken(user.nombreUsuario)

                    // Guardar el refresh token en la base de datos o en algún almacenamiento seguro
                    userService.storeRefreshToken(nombreUsuario, refreshToken)

                    call.respond(HttpStatusCode.OK, UsuarioAuthResponse(
                        id = user.id.value,
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        tipoUsuario = user.tipoUsuario,
                        idClub = user.idClub?.value
                    ))
                } else {
                    call.respond("Usuario o contraseña incorrectos")
                }
            }

            post("/refresh"){
                val request = call.receive<Map<String, String>>()
                val refreshToken = request["refresh_token"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing refresh token")

                // Verifica que el Refresh Token sea válido
                val username = AuthConfig.verifyRefreshToken(refreshToken)

                if (username != null) {
                    val storedRefreshToken = userService.getRefreshToken(username)
                    if (storedRefreshToken == refreshToken) {
                        // Genera un nuevo Access Token
                        val newAccessToken = AuthConfig.generateToken(username)

                        call.respond(mapOf("new_access_token" to newAccessToken))
                    } else {
                        // Si el refresh token no coincide, se rechaza
                        call.respond(HttpStatusCode.Unauthorized, "Refresh token inválido")
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid or expired refresh token")
                }
            }
        }

        authenticate("auth-jwt") {
            route("/auth") {
                post("/logout") {
                    val username = call.principal<JWTPrincipal>()?.payload?.getClaim("usuario")?.asString()

                    if (username != null) {
                        userService.deleteRefreshToken(username)

                        call.respond(HttpStatusCode.OK, "Sesión cerrada correctamente.")
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, "Usuario no autenticado.")
                    }
                }
            }
        }

    }
}