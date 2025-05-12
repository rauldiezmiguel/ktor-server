package services

import model.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import model.JugadorDAO
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnSet
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.selectAll

class EstadisticasEquipoService(
    private val jugadorService: EstadisticasJugadorService = EstadisticasJugadorService()
) {

    fun getEstadisticasEquipoByTemporada(idEquipo: Int, idTemporada: Int): List<EstadisticasEquipoDAO> = transaction {
        EstadisticasEquipoDAO.find { (EstadisticasEquipo.idEquipo eq idEquipo) and (EstadisticasEquipo.idTemporada eq idTemporada) }.toList()
    }

    fun getEstadisticaEquipoConTotales(
        idEquipo: Int,
        idTemporada: Int,
        nomEstadistica: String
    ): EstadisticaEquipoDTO = transaction {
        val jugadores = JugadorDAO
            .find { Jugadores.idEquipo eq idEquipo }
            .toList()

        val estadisticaNormalizada = nomEstadistica.trim().lowercase()

        var estadisticaNombre = nomEstadistica

        if (estadisticaNormalizada == "partidostotales") {
            estadisticaNombre = "partidosjugados"
        } else if (estadisticaNormalizada == "minutostotales") {
            estadisticaNombre = "minutosjugados"
        }


        val detallesPorJugador = jugadores.mapNotNull { jugador ->
            val detalles = jugadorService.getDetalleEstadisticaJugador(
                idJugador = jugador.id.value,
                idEquipo = idEquipo,
                idTemporada = idTemporada,
                nomEstadistica = estadisticaNombre
            )
            val totalValor = detalles.sumOf { it.valorEstadistica }
            if (totalValor > 0) {
                EstadisticaEquipoDetalleDTO(
                    idJugador = jugador.id.value,
                    idEquipo = idEquipo,
                    nombreJugador = jugador.nombreJugador,
                    nomEstadistica = estadisticaNombre,
                    valorEstadistica = totalValor
                )
            } else null
        }

        val listaValoresTotales = EstadisticasEquipoDAO.find { (EstadisticasEquipo.idEquipo eq idEquipo) and (EstadisticasEquipo.idTemporada eq idTemporada) }.toList()
        val estadisticasEquipo = listaValoresTotales.firstOrNull()
        val totalEquipo = when (nomEstadistica) {
            "goles" -> estadisticasEquipo?.golesTotales ?: 0
            "asistencias" -> estadisticasEquipo?.asistenciasTotal ?: 0
            "minutostotales" -> estadisticasEquipo?.minutosTotales ?: 0
            "partidostotales" -> estadisticasEquipo?.partidosTotales ?: 0
            "tarjetasamarillas" -> estadisticasEquipo?.tarjetasAmarillasTotales ?: 0
            "tarjetasrojas" -> estadisticasEquipo?.tarjetasRojasTotales ?: 0
            else -> 0
        }

        EstadisticaEquipoDTO(
            nomEstadistica = nomEstadistica,
            totalEquipo = totalEquipo,
            detallesPorJugador = detallesPorJugador.sortedByDescending { it.valorEstadistica }
        )
    }
}