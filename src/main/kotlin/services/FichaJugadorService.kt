package services

import model.Equipos
import model.FichaJugador
import model.FichaJugadorDAO
import model.FichaJugadorDTO
import model.Jugadores
import model.Temporadas
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

private val idTemporadas: Int = 1

class FichaJugadorService {
    fun getFichaJugadorByIdJugador(idJugador: Int): FichaJugadorDTO = transaction {
        FichaJugadorDAO.find { FichaJugador.idJugador eq idJugador }.first().toDTO()
    }

    fun getFichasJugadoresEquipo(idEquipo: Int): List<FichaJugadorDTO> = transaction {
        FichaJugadorDAO.find { FichaJugador.idEquipo eq idEquipo }.map { it.toDTO() }.toList()
    }

    fun createdFichaJugador(
        idJugador: Int,
        idEquipo: Int,
        piernaHabil: String?,
        caracteristicasFisicas: String?,
        caracteristicasTacticas: String?,
        caracteristicasTecnicas: String?,
        conductaEntrenamiento: String?,
        conductaConCompañeros: String?
    ): FichaJugadorDAO = transaction {
        FichaJugadorDAO.new {
            this.idJugador = EntityID(idJugador, Jugadores)
            this.idEquipo = EntityID(idEquipo, Equipos)
            this.idTemporada = EntityID(idTemporadas, Temporadas)
            this.piernaHabil = piernaHabil
            this.caracteristicasFisicas = caracteristicasFisicas
            this.caracteristicasTacticas = caracteristicasTacticas
            this.caracteristicasTecnicas = caracteristicasTecnicas
            this.conductaEntrenamiento = conductaEntrenamiento
            this.conductaConCompañeros = conductaConCompañeros
        }
    }

    fun deleteFichaJugador(id: Int): Boolean = transaction {
        val fichaJugadorDelete = FichaJugadorDAO.findById(id) ?: return@transaction false
        fichaJugadorDelete.delete()
        true
    }
}