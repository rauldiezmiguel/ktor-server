package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Temporadas : IntIdTable("temporadas") {
    val añoInicio = integer("año_inicio").uniqueIndex();
    val añoFin = integer("año_fin").uniqueIndex();
    val activa = bool("activa")
}

class TemporadaDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TemporadaDAO>(Temporadas)

    var añoInicio by Temporadas.añoInicio
    var añoFin by Temporadas.añoFin
    var activa by Temporadas.activa

    fun toDTO(): TemporadaDTO {
        return TemporadaDTO(
            id = this.id.value,
            añoInicio = this.añoInicio,
            añoFin = this.añoFin,
            activa = this.activa
        )
    }
}

@Serializable
data class TemporadaDTO(
    val id: Int,
    val añoInicio: Int,
    val añoFin: Int,
    val activa: Boolean
)

@Serializable
data class PromedioMensualDTO(
    val año: Int,
    val mes: Int,
    val comportamiento: Double,
    val tecnica: Double,
    val tactica: Double
)