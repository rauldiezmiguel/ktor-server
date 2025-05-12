package model

import kotlinx.serialization.Serializable
import model.EstadisticasJugador.default
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object EstadisticasTotalJugador : IntIdTable("estadisticas_total_jugador") {
    val idJugador = reference("id_jugador", Jugadores, onDelete = ReferenceOption.CASCADE)
    val idTemporada = reference("id_temporada", Temporadas, onDelete = ReferenceOption.CASCADE)
    val minutosJugados = integer("minutos_jugados").default(0)
    val goles = integer("goles").default(0)
    val asistencias = integer("asistencias").default(0)
    val partidosComoTitular = integer("partidos_como_titular").default(0)
    val tarjetasAmarillas = integer("tarjetas_amarillas").default(0)
    val tarjetasRojas = integer("tarjetas_rojas").default(0)
    val partidosJugados = integer("partidos_jugados").default(0)
    init {
        uniqueIndex(idJugador, idTemporada)
    }
}

class EstadisticasTotalJugadorDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EstadisticasTotalJugadorDAO>(EstadisticasTotalJugador)

    var idJugador by EstadisticasTotalJugador.idJugador
    var idTemporada by EstadisticasTotalJugador.idTemporada
    var minutosJugados by EstadisticasTotalJugador.minutosJugados
    var goles by EstadisticasTotalJugador.goles
    var asistencias by EstadisticasTotalJugador.asistencias
    var partidosComoTitular by EstadisticasTotalJugador.partidosComoTitular
    var tarjetasAmarillas by EstadisticasTotalJugador.tarjetasAmarillas
    var tarjetasRojas by EstadisticasTotalJugador.tarjetasRojas
    var partidosJugados by EstadisticasTotalJugador.partidosJugados

    fun toDTO(): EstadisticasTotalJugadorDTO {
        return EstadisticasTotalJugadorDTO(
            id = this.id.value,
            idJugador = this.idJugador.value,
            idTemporada = this.idTemporada.value,
            minutosJugados = this.minutosJugados,
            goles = this.goles,
            asistencias = this.asistencias,
            partidosComoTitular = this.partidosComoTitular,
            tarjetasAmarillas = this.tarjetasAmarillas,
            tarjetasRojas = this.tarjetasRojas,
            partidosJugados = this.partidosJugados
        )
    }
}

@Serializable
data class EstadisticasTotalJugadorDTO(
    val id: Int,
    val idJugador: Int,
    val idTemporada: Int,
    val minutosJugados: Int,
    val goles: Int,
    val asistencias: Int,
    val partidosComoTitular: Int,
    val tarjetasAmarillas: Int,
    val tarjetasRojas: Int,
    val partidosJugados: Int
)

