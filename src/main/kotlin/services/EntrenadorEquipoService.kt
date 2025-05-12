package services

import model.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class EntrenadorEquipoService() {
    fun getAllEntrenadoresEquipo(): List<EntrenadorEquipoDAO> = transaction {
        EntrenadorEquipoDAO.all().toList()
    }

    fun getEntrenadoresByEquipo(idEquipo: Int): List<EntrenadorEquipoDAO> = transaction {
        // Buscamos las relaciones donde el club esté asignado

        EntrenadorEquipoDAO.find { EntrenadorEquipo.idEquipo eq idEquipo }.toList()
    }

    fun getEquiposByEntrenador(idEntrenador: Int): List<EquipoDAO> = transaction {
        // Buscamos las relaciones donde el entrenador esté asignado
        val relaciones = EntrenadorEquipoDAO.find { EntrenadorEquipo.idEntrenador eq idEntrenador }

        // Extraemos los IDs de los equipos de esas relaciones
        val idsEquipos = relaciones.map { it.idEquipo.value }

        // Buscar en EquipoDAO los equipos con esos IDs
        EquipoDAO.find { Equipos.id inList idsEquipos }.toList()
    }

    fun getEquiposByClub(idClub: Int): List<EquipoDAO> = transaction {
        // Buscar los equipos que pertenecen al club dado
        EquipoDAO.find { Equipos.id eq idClub }.toList()
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
}