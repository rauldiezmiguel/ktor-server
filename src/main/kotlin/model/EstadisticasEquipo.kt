package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object EstadisticasEquipo : IntIdTable("estadisticas_equipo") {
    val idEquipo = reference("id_equipo", Equipos, onDelete = ReferenceOption.CASCADE)
    val idTemporada = reference("id_temporada", Temporadas, onDelete = ReferenceOption.CASCADE)
    val golesTotales = integer("goles_totales").default(0)
    val asistenciasTotales = integer("asistencias_totales").default(0)
    val minutosTotales = integer("minutos_totales").default(0)
    val tarjetasAmarillasTotales = integer("tarjetas_amarillas_totales").default(0)
    val tarjetasRojasTotales = integer("tarjetas_rojas_totales").default(0)
    val partidosTotales = integer("partidos_totales").default(0)
    init {
        uniqueIndex(idEquipo, idTemporada)
    }
}

class EstadisticasEquipoDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EstadisticasEquipoDAO>(EstadisticasEquipo)

    var idEquipo by EstadisticasEquipo.idEquipo
    var idTemporada by EstadisticasEquipo.idTemporada
    var golesTotales by EstadisticasEquipo.golesTotales
    var asistenciasTotal by EstadisticasEquipo.asistenciasTotales
    var minutosTotales by EstadisticasEquipo.minutosTotales
    var tarjetasAmarillasTotales by EstadisticasEquipo.tarjetasAmarillasTotales
    var tarjetasRojasTotales by EstadisticasEquipo.tarjetasRojasTotales
    var partidosTotales by EstadisticasEquipo.partidosTotales

    fun toDTO(): EstadisticasEquipoDTO {
        return EstadisticasEquipoDTO(
            id = this.id.value,
            idEquipo = this.idEquipo.value,
            idTemporada = this.idTemporada.value,
            golesTotales = this.golesTotales,
            asistenciasTotales = this.asistenciasTotal,
            minutosTotales = this.minutosTotales,
            tarjetasAmarillasTotales = this.tarjetasAmarillasTotales,
            tarjetasRojasTotales = this.tarjetasRojasTotales,
            partidosTotales = this.partidosTotales
        )
    }
}

@Serializable
data class EstadisticasEquipoDTO(
    val id: Int,
    val idEquipo: Int,
    val idTemporada: Int,
    val golesTotales: Int,
    val asistenciasTotales: Int,
    val minutosTotales: Int,
    val tarjetasAmarillasTotales: Int,
    val tarjetasRojasTotales: Int,
    val partidosTotales: Int
)

@Serializable
data class EstadisticaEquipoDetalleDTO(
    val idJugador: Int,
    val idEquipo: Int,
    val nombreJugador: String,
    val valorEstadistica: Int,
    val nomEstadistica: String
)

@Serializable
data class EstadisticaEquipoDTO(
    val nomEstadistica: String,
    val totalEquipo: Int,
    val detallesPorJugador: List<EstadisticaEquipoDetalleDTO>
)