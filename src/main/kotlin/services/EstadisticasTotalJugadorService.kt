package services

import model.EstadisticasTotalJugador
import model.EstadisticasTotalJugadorDAO
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class EstadisticasTotalJugadorService {
    fun getEstadisticasJugadorByTemporada(idJugador: Int, idTemporada: Int): List<EstadisticasTotalJugadorDAO> = transaction {
        EstadisticasTotalJugadorDAO.find { (EstadisticasTotalJugador.idJugador eq idJugador) and (EstadisticasTotalJugador.idTemporada eq idTemporada) }.toList()
    }
}