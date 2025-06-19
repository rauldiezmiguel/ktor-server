package services

import model.Equipos
import model.FichaJugador
import model.FichaJugadorDAO
import model.FichaJugadorDTO
import model.Jugadores
import model.TemporadaDAO
import model.Temporadas
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class FichaJugadorService {
    fun getFichaJugadorByIdJugador(idJugador: Int): FichaJugadorDTO? = transaction {
        val temporadaActivaId = getTemporadaActivaId() ?: return@transaction null
        FichaJugadorDAO.find {
            (FichaJugador.idJugador eq idJugador) and (FichaJugador.idTemporada eq temporadaActivaId)
        }.firstOrNull()?.toDTO() as FichaJugadorDTO
    }

    fun getFichasJugadoresEquipo(idEquipo: Int): List<FichaJugadorDTO> = transaction {
        val temporadaActivaId = getTemporadaActivaId() ?: return@transaction emptyList()
        FichaJugadorDAO.find {
            (FichaJugador.idEquipo eq idEquipo) and (FichaJugador.idTemporada eq temporadaActivaId)
        }.map { it.toDTO() }
    }

    fun createdFichaJugador(
        idJugador: Int,
        idEquipo: Int,
        piernaHabil: String?,
        caracteristicasFisicas: String?,
        caracteristicasTacticas: String?,
        caracteristicasTecnicas: String?,
        conductaEntrenamiento: String?,
        conductaConCompañeros: String?,
        observacionFinal: String?
    ): FichaJugadorDAO = transaction {
        val temporadaId = requireTemporadaActivaId()

        FichaJugadorDAO.new {
            this.idJugador = EntityID(idJugador, Jugadores)
            this.idEquipo = EntityID(idEquipo, Equipos)
            this.idTemporada = EntityID(temporadaId, Temporadas)
            this.piernaHabil = piernaHabil
            this.caracteristicasFisicas = caracteristicasFisicas
            this.caracteristicasTacticas = caracteristicasTacticas
            this.caracteristicasTecnicas = caracteristicasTecnicas
            this.conductaEntrenamiento = conductaEntrenamiento
            this.conductaConCompañeros = conductaConCompañeros
            this.observacionFinal = observacionFinal
        }
    }

    fun deleteFichaJugador(id: Int): Boolean = transaction {
        val fichaJugadorDelete = FichaJugadorDAO.findById(id) ?: return@transaction false
        fichaJugadorDelete.delete()
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