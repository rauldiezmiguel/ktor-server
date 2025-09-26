package services

import model.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class EntrenadorEquipoService() {
    fun getAllEntrenadoresEquipo(): List<EntrenadorEquipoDAO> = transaction {
        val temporadaActivaId = getTemporadaActivaId() ?: return@transaction emptyList()
        EntrenadorEquipoDAO.find { EntrenadorEquipo.idTemporada eq temporadaActivaId }.toList()
    }

    /*
    fun getEntrenadoresByEquipo(idEquipo: Int): List<EntrenadorEquipoDAO> = transaction {
        // Buscamos las relaciones donde el club esté asignado

        EntrenadorEquipoDAO.find { EntrenadorEquipo.idEquipo eq idEquipo }.toList()
    }

     */

    fun getEntrenadoresByEquipo(idEquipo: Int): List<UsuarioDAO> = transaction {
        val temporadaActivaId = getTemporadaActivaId() ?: return@transaction emptyList()

        // Verificamos que el equipo pertenece a la temporada activa
        val equipo = EquipoDAO.find {
            (Equipos.id eq idEquipo) and (Equipos.idTemporada eq temporadaActivaId)
        }.firstOrNull() ?: return@transaction emptyList()

        // Obtenemos las relaciones del equipo
        val relaciones = EntrenadorEquipoDAO.find { EntrenadorEquipo.idEquipo eq equipo.id }

        val idsEntrenadores = relaciones.map { it.idEntrenador.value }

        // Devolvemos directamente los entrenadores (UsuarioDAO)
        UsuarioDAO.find { Usuarios.id inList idsEntrenadores }.toList()
    }

    fun getEquiposByEntrenador(idEntrenador: Int): List<EquipoDAO> = transaction {
        // Buscamos las relaciones donde el entrenador esté asignado
        val relaciones = EntrenadorEquipoDAO.find { EntrenadorEquipo.idEntrenador eq idEntrenador }

        // Extraemos los IDs de los equipos de esas relaciones
        val idsEquipos = relaciones.map { it.idEquipo.value }

        val temporadaActivaId = getTemporadaActivaId() ?: return@transaction emptyList()

        // Buscar en EquipoDAO los equipos con esos IDs
        EquipoDAO.find { (Equipos.id inList idsEquipos) and (Equipos.idTemporada eq temporadaActivaId) }.toList()
    }

    fun getEquiposByClub(idClub: Int): List<EquipoDAO> = transaction {
        val temporadaActivaId = getTemporadaActivaId() ?: return@transaction emptyList()
        // Buscar los equipos que pertenecen al club dado
        EquipoDAO.find { (Equipos.id eq idClub) and (Equipos.idTemporada eq temporadaActivaId) }.toList()
    }

    fun addEntrenadorToEquipo(idEntrenador: Int, idEquipo: Int, idTemporada: Int): EntrenadorEquipoDAO = transaction {
        EntrenadorEquipoDAO.new {
            this.idEntrenador = EntityID(idEntrenador, Usuarios)
            this.idEquipo = EntityID(idEquipo, Equipos)
            this.idTemporada = EntityID(idTemporada, Temporadas)
        }
    }

    fun deleteEntrenadorDeEquipo(idEntrenador: Int, idEquipo: Int): Boolean = transaction {
        val relacion = EntrenadorEquipoDAO.find { (EntrenadorEquipo.idEntrenador eq idEntrenador) and (EntrenadorEquipo.idEquipo eq idEquipo) }.singleOrNull()

        relacion?.delete() != null
    }

    private fun getTemporadaActivaId(): Int? = transaction {
        TemporadaDAO.find { Temporadas.activa eq true }
            .maxByOrNull { it.añoInicio }
            ?.id?.value
    }

    private fun requireTemporadaActivaId(): Int = getTemporadaActivaId()
        ?: error("No hay temporada activa configurada")
}