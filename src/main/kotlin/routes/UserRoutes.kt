package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import model.ChangePasswordRequest
import services.UserService

fun Application.userRoutes() {
    val userService = UserService()

    routing {
        authenticate("auth-jwt") {
            route("/usuarios") {
                get {
                    val users = userService.getAllUsers()
                    call.respond(users.map { it.toDTO() })// Convertimos a JSON
                }

                get("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val user = id?.let { userService.getUserById(it) }
                    if (user != null) {
                        call.respond(user.toDTO())
                    } else {
                        call.respond("Usuario no encontrado")
                    }
                }

                get("/clubes/{idClub}") {
                    val idClub = call.parameters["idClub"]?.toIntOrNull()

                    if (idClub == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID club invalido")
                        return@get
                    }

                    val usuarios = userService.getUserByClub(idClub).map { it.toDTO() }
                    call.respond(usuarios)
                }

                get("/{nombreUsuario}") {
                    val nombreUsuario = call.parameters["nombreUsuario"]
                    if (nombreUsuario == null) {
                        call.respond(HttpStatusCode.BadRequest, "Nombre no encontrado")
                        return@get
                    }

                    val usuario = userService.getUserByNombre(nombreUsuario)
                    if (usuario == null) {
                        call.respond(HttpStatusCode.BadRequest, "Nombre no encontrado")
                        return@get
                    } else {
                        call.respond(usuario.toDTO())
                    }
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()

                    val usuarioDeleted = id?.let { userService.deleteUser(it) } ?: false
                    if (usuarioDeleted) {
                        call.respond(HttpStatusCode.OK, "Usuario eliminado correctamente")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Usuario no encontrado")
                    }
                }

                get("/perfil/{idUsuario}") {
                    val id = call.parameters["idUsuario"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID de usuario inválido")
                        return@get
                    }

                    val perfil = userService.getPerfilUsuario(id)
                    if (perfil != null) {
                        call.respond(HttpStatusCode.OK, perfil)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Usuario no encontrado")
                    }
                }

                post("/change-password") {
                    val principal = call.principal<JWTPrincipal>()
                    val username = principal?.payload?.getClaim("usuario")?.asString()
                        ?: return@post call.respond(HttpStatusCode.Unauthorized, "Usuario no autenticado")

                    val request = call.receive<ChangePasswordRequest>()

                    val updated = userService.changePassword(username, request.currentPassword, request.newPassword)

                    if (updated) {
                        call.respond(HttpStatusCode.OK, "Contraseña cambiada con éxito")
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Contraseña actual incorrecta o usuario no encontrado")
                    }
                }
            }
        }
    }
}