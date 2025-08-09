package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Usuarios : IntIdTable("usuarios") {
    val nombreUsuario = text("nombre_usuario").uniqueIndex()
    val passWrd = text("passwrd") // Aquí guardamos la contraseña ya hasehada
    val tipoUsuario = text("tipo_usuario").check{ it inList listOf("entrenador", "coordindaor") } // entrenador o coordinador
    val idClub = reference("id_club", Clubes, onDelete = ReferenceOption.CASCADE).nullable() // FK Clubes
    val refreshToken = text("refreshtoken").nullable()
}

class UsuarioDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UsuarioDAO>(Usuarios)

    var nombreUsuario by Usuarios.nombreUsuario
    var passWrd by Usuarios.passWrd
    var tipoUsuario by Usuarios.tipoUsuario
    var idClub by Usuarios.idClub
    var refreshToken by Usuarios.refreshToken

    fun toDTO(): UsuarioDTO {
        return UsuarioDTO(
            id = this.id.value,
            nombreUsuario = this.nombreUsuario,
            tipoUsuario = this.tipoUsuario,
            idClub = this.idClub?.value
        )
    }
}

@Serializable
data class UsuarioDTO(
    val id: Int?,
    val nombreUsuario: String,
    val tipoUsuario: String,
    val idClub: Int?
)

@Serializable
data class UsuarioAuthResponse(
    val id: Int?,
    val accessToken: String,
    val refreshToken: String,
    val tipoUsuario: String,
    val idClub: Int?
)

@Serializable
data class ClubPerfilDTO(
    val id: Int,
    val nombre: String
)

@Serializable
data class EquipoPerfilDTO(
    val id: Int,
    val nombreEquipo: String
    // añade aquí los campos que necesites
)

@Serializable
data class PerfilUsuarioDTO(
    val id: Int,
    val nombreUsuario: String,
    val tipoUsuario: String,
    val club: ClubPerfilDTO?,
    val equipos: List<EquipoPerfilDTO>
)

@Serializable
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)