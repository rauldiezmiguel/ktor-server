package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object EstadisticasPartido : IntIdTable("estadisticas_partido") {
    val idPartido = reference("id_partido", Partidos, onDelete = ReferenceOption.CASCADE)
    val idTemporada = reference("id_temporada", Temporadas, onDelete = ReferenceOption.CASCADE)
    val golesTotalesPartido = integer("goles_totales_partido").default(0)
    val asistenciasTotalesPartido = integer("asistencias_totales_partido").default(0)
    val minutosTotalesPartido = integer("minutos_totales_partido").default(0)
    val tarjetasAmarillasTotalesPartido = integer("tarjetas_amarillas_totales_partido").default(0)
    val tarjetasRojasTotalesPartido = integer("tarjetas_rojas_totales_partido").default(0)
    init {
        uniqueIndex(idPartido, idTemporada)
    }
}

class EstadisticasPartidoDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EstadisticasPartidoDAO>(EstadisticasPartido)

    var idPartido by EstadisticasPartido.idPartido
    var idTemporada by EstadisticasPartido.idTemporada
    var golesTotalesPartido by EstadisticasPartido.golesTotalesPartido
    var asistenciasTotalesPartido by EstadisticasPartido.asistenciasTotalesPartido
    var minutosTotalesPartido by EstadisticasPartido.minutosTotalesPartido
    var tarjetasAmarillasTotalesPartido by EstadisticasPartido.tarjetasAmarillasTotalesPartido
    var tarjetasRojasTotalesPartido by EstadisticasPartido.tarjetasRojasTotalesPartido

    fun toDTO(): EstadisticasPartidoDTO {
        return EstadisticasPartidoDTO(
            id = this.id.value,
            idPartido  = this.idPartido.value,
            idTemporada = this.idTemporada.value,
            golesTotalesPartido = this.golesTotalesPartido,
            asistenciasTotalesPartido = this.asistenciasTotalesPartido,
            minutosTotalesPartido = this.minutosTotalesPartido,
            tarjetasAmarillasTotalesPartido = this.tarjetasAmarillasTotalesPartido,
            tarjetasRojasTotalesPartido = this.tarjetasRojasTotalesPartido
        )
    }
}

@Serializable
data class EstadisticasPartidoDTO(
    val id: Int,
    val idPartido: Int,
    val idTemporada: Int,
    val golesTotalesPartido: Int,
    val asistenciasTotalesPartido: Int,
    val minutosTotalesPartido: Int,
    val tarjetasAmarillasTotalesPartido: Int,
    val tarjetasRojasTotalesPartido: Int
)

@Serializable
data class EstadisticaPartidoDTO(
    val idPartido: Int,
    val valorEstadistica: Int,
    val nomEstadistica: String,
    val nombreRival: String?
)

@Serializable
data class ResumenParticipacionJugadorDto(
    val idPartido: Int,
    val totalPartidosEquipo: Int,
    val valorEstadistica: Int,
    val nomEstadistica: String,
    val nombreRival: String?
)