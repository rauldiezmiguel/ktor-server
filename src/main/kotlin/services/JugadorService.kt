package services

import model.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class JugadorService {

    fun getJugadorById(id: Int): JugadorDAO? = transaction {
        val temporadaActivaId = getTemporadaActivaId() ?: return@transaction null
        val jugador = JugadorDAO.findById(id)
        if (jugador?.idTemporada?.value == temporadaActivaId) jugador else null
    }

    fun getJugadorByEquipo(idEquipo: Int): List<JugadorDAO> = transaction {
        val temporadaActivaId = getTemporadaActivaId() ?: return@transaction emptyList()
        JugadorDAO.find {
            (Jugadores.idEquipo eq idEquipo) and (Jugadores.idTemporada eq temporadaActivaId)
        }.toList()
    }

    fun createJugador(nombre: String, dorsal: Int, posicion: String?, idEquipo: Int?): JugadorDAO = transaction {
        val temporadaId = requireTemporadaActivaId()

        JugadorDAO.new {
            this.nombreJugador = nombre
            this.dorsal = dorsal
            this.posicion = posicion
            this.idEquipo = EntityID( idEquipo ?: error("El ID del equipo no puede ser nulo"), Equipos)
            this.idTemporada = EntityID(temporadaId, Temporadas)
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

    private fun getTemporadaActivaId(): Int? = transaction {
        TemporadaDAO.find { Temporadas.activa eq true }
            .maxByOrNull { it.añoInicio }
            ?.id?.value
    }

    private fun requireTemporadaActivaId(): Int = getTemporadaActivaId()
        ?: error("No hay temporada activa")
}