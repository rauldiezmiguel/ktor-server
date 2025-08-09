package services

import authentication.PasswordHasher
import model.ClubDAO
import model.ClubDTO
import model.ClubPerfilDTO
import model.Clubes
import model.EntrenadorEquipo
import model.EntrenadorEquipoDAO
import model.EntrenadorEquipoDTO
import model.EquipoDAO
import model.EquipoDTO
import model.EquipoPerfilDTO
import model.Equipos
import model.PerfilUsuarioDTO
import model.UsuarioDAO
import model.Usuarios
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.dao.id.EntityID

class UserService {
    fun getAllUsers(): List<UsuarioDAO> = transaction {
        UsuarioDAO.all().toList()
    }

    fun getUserById(id: Int): UsuarioDAO? = transaction {
        UsuarioDAO.findById(id)
    }

    fun getUserByNombre(nombre: String): UsuarioDAO? = transaction {
        UsuarioDAO.find { Usuarios.nombreUsuario eq nombre }.singleOrNull()
    }

    fun getUserByClub(idClub: Int): List<UsuarioDAO> = transaction {
        val temporadaActivaId = getTemporadaActivaId() ?: return@transaction emptyList()

        // Filtramos usuarios por club y relaciones entrenador-equipo que tengan equipos en temporada activa
        UsuarioDAO.all().filter { usuario ->
            usuario.idClub?.value == idClub &&
                    EntrenadorEquipoDAO.find {
                        (EntrenadorEquipo.idEntrenador eq usuario.id.value)
                    }.any { relacion ->
                        val equipo = EquipoDAO.findById(relacion.idEquipo.value)
                        equipo?.idTemporada?.value == temporadaActivaId
                    }
        }
    }

    fun createUser(nombreUsuario: String, passWrd: String, tipoUsuario: String, idClub: Int?): UsuarioDAO = transaction{
        UsuarioDAO.new {
            this.nombreUsuario = nombreUsuario
            this.passWrd = passWrd
            this.tipoUsuario = tipoUsuario
            this.idClub = idClub?.let { EntityID(it, Clubes) }
        }
    }

    fun authenticateUser(nombreUsuario: String, passWrd: String): UsuarioDAO? = transaction {
        val user = UsuarioDAO.find { Usuarios.nombreUsuario eq nombreUsuario }.singleOrNull()
        if (user != null && PasswordHasher.verifyPassword(passWrd, user.passWrd)){
            return@transaction user
        } else {
            return@transaction null
        }
    }

    fun deleteUser(id: Int): Boolean = transaction {
        UsuarioDAO.findById(id)?.delete() != null
    }

    fun storeRefreshToken(username: String, refreshToken: String) = transaction {
        val user = UsuarioDAO.find { Usuarios.nombreUsuario eq username }.singleOrNull()
        user?.refreshToken = refreshToken
    }

    fun getRefreshToken(username: String): String? = transaction {
        val user = getUserByNombre(username)
        return@transaction user?.refreshToken
    }

    fun deleteRefreshToken(username: String) = transaction {
        val user = getUserByNombre(username)
        user?.refreshToken = null
    }

    fun changePassword(username: String, currentPassword: String, newPassword: String): Boolean = transaction {
        val user = UsuarioDAO.find { Usuarios.nombreUsuario eq username }.singleOrNull()
            ?: return@transaction false

        // Verificar contraseña actual
        if (!PasswordHasher.verifyPassword(currentPassword, user.passWrd)) {
            return@transaction false
        }

        // Hashear y guardar la nueva contraseña
        val hashedNewPassword = PasswordHasher.hashPassword(newPassword)
        user.passWrd = hashedNewPassword
        true
    }

    fun getPerfilUsuario(idUsuario: Int): PerfilUsuarioDTO? = transaction {
        val usuario = UsuarioDAO.findById(idUsuario) ?: return@transaction null
        val temporadaActivaId = getTemporadaActivaId()

        // Club (puede ser null)
        val clubDto = usuario.idClub?.let { clubId ->
            ClubDAO.findById(clubId)?.let { club ->
                ClubPerfilDTO(id = club.id.value, nombre = club.nombreClub)
            }
        }

        // 1) Recuperamos las relaciones
        val relaciones = EntrenadorEquipoDAO.find {
            EntrenadorEquipo.idEntrenador eq idUsuario
        }.toList()

        // 2) Por cada relación, buscamos el Equipo y extraemos su nombre
        val equiposDto = relaciones.mapNotNull { rel ->
            EquipoDAO.findById(rel.idEquipo.value)?.takeIf { equipo ->
                equipo.idTemporada.value == temporadaActivaId
            }?.let { equipo ->
                EquipoPerfilDTO(
                    id = equipo.id.value,
                    nombreEquipo = equipo.nombreEquipo
                )
            }
        }

        // Armamos el DTO completo
        PerfilUsuarioDTO(
            id            = usuario.id.value,
            nombreUsuario = usuario.nombreUsuario,
            tipoUsuario   = usuario.tipoUsuario,
            club          = clubDto,
            equipos       = equiposDto
        )
    }

    private fun getTemporadaActivaId(): Int? = transaction {
        model.TemporadaDAO.find { model.Temporadas.activa eq true }
            .maxByOrNull { it.añoInicio }
            ?.id
            ?.value
    }
}