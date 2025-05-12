package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object EstadisticasJugador : IntIdTable("estadisticas_jugador") {
    val idJugador = reference("id_jugador", Jugadores, onDelete = ReferenceOption.CASCADE)
    val idPartido = reference("id_partido", Partidos, onDelete = ReferenceOption.CASCADE)
    val idTemporada = reference("id_temporada", Temporadas, onDelete = ReferenceOption.CASCADE)
    val minutosJugados = integer("minutos_jugados").default(0)
    val goles = integer("goles").default(0)
    val asistencias = integer("asistencias").default(0)
    val titular = bool("titular").default(false)
    val tarjetasAmarillas = integer("tarjetas_amarillas").default(0)
    val tarjetasRojas = integer("tarjetas_rojas").default(0)
    val partidoJugado =  bool("partido_jugado").default(false)
    init {
        uniqueIndex(idJugador, idPartido)
    }
}

class EstadisticasJugadorDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EstadisticasJugadorDAO>(EstadisticasJugador)

    var idJugador by EstadisticasJugador.idJugador
    var idPartido by EstadisticasJugador.idPartido
    var idTemporada by EstadisticasJugador.idTemporada
    var minutosJugados by EstadisticasJugador.minutosJugados
    var goles by EstadisticasJugador.goles
    var asistencias by EstadisticasJugador.asistencias
    var titular by EstadisticasJugador.titular
    var tarjetasAmarillas by EstadisticasJugador.tarjetasAmarillas
    var tarjetasRojas by EstadisticasJugador.tarjetasRojas
    var partidoJugado by EstadisticasJugador.partidoJugado

    fun toDTO(): EstadisticasJugadorDTO {
        return EstadisticasJugadorDTO(
            id = this.id.value,
            idJugador = this.idJugador.value,
            idPartido = this.idPartido.value,
            idTemporada = this.idTemporada.value,
            minutosJugados = this.minutosJugados,
            goles = this.goles,
            asistencias = this.asistencias,
            titular = this.titular,
            tarjetasAmarillas = this.tarjetasAmarillas,
            tarjetasRojas = this.tarjetasRojas,
            partidoJugado = this.partidoJugado
        )
    }
}

@Serializable
data class EstadisticasJugadorDTO(
    val id: Int,
    val idJugador: Int,
    val idPartido: Int,
    val idTemporada: Int,
    val minutosJugados: Int,
    val goles: Int,
    val asistencias: Int,
    val titular: Boolean,
    val tarjetasAmarillas: Int,
    val tarjetasRojas: Int,
    val partidoJugado: Boolean
)