package services

import model.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class JugadorService {

    private val idTemporadas: Int = 1

    fun getJugadorById(id: Int): JugadorDAO? = transaction {
        JugadorDAO.findById(id)
    }

    fun getJugadorByEquipo(idEquipo: Int): List<JugadorDAO> = transaction {
        JugadorDAO.find { Jugadores.idEquipo eq idEquipo }.toList()
    }

    fun createJugador(nombre: String, dorsal: Int, posicion: String?, idEquipo: Int?): JugadorDAO = transaction {
        JugadorDAO.new {
            this.nombreJugador = nombre
            this.dorsal = dorsal
            this.posicion = posicion
            this.idEquipo = EntityID( idEquipo ?: error("El ID del equipo no puede ser nulo"), Equipos)
            this.idTemporada = EntityID(idTemporadas, Temporadas)
        }
    }

    fun updateJugador(id: Int, nombre: String, dorsal: Int, posicion: String?, idEquipo: Int): Boolean = transaction {
        val jugador = JugadorDAO.findById(id) ?: return@transaction false

        jugador.nombreJugador = nombre
        jugador.dorsal = dorsal
        jugador.posicion = posicion
        idEquipo.let { jugador.idEquipo = EntityID(it, Equipos) }
        true
    }

    fun deleteJugador(id: Int): Boolean = transaction {
        val jugadorDelete = JugadorDAO.findById(id) ?: return@transaction false
        jugadorDelete.delete()
        true
    }
}