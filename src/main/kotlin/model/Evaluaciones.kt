package model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.date
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toJavaLocalDate

object Evaluaciones : IntIdTable("evaluaciones") {
    val idJugador = reference("id_jugador", Jugadores, onDelete = ReferenceOption.CASCADE) // FK a Jugadores
    val idTemporada = reference("id_temporada", Temporadas, onDelete = ReferenceOption.CASCADE)
    val fecha = date("fecha").nullable()
    val comportamiento = integer("comportamiento").check { it.between(1, 10) } // del 1 al 10
    val tecnica = integer("tecnica").check { it.between(1, 10) } // del 1 al 10
    val tactica = integer("tactica").check { it.between(1, 10) } // del 1 al 10
    val observaciones = text("observaciones").nullable()
}

class EvaluacionesDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EvaluacionesDAO>(Evaluaciones)

    var idJugador by Evaluaciones.idJugador
    var idTemporada by Evaluaciones.idTemporada
    var fecha by Evaluaciones.fecha
    var comportamiento by Evaluaciones.comportamiento
    var tecnica by Evaluaciones.tecnica
    var tactica by Evaluaciones.tactica
    var observaciones by Evaluaciones.observaciones

    fun toDTO(): EvaluacionesDTO {
        return EvaluacionesDTO(
            id = this.id.value,
            idJugador = this.idJugador.value,
            idTemporada = this.idTemporada?.value,
            fecha = this.fecha?.toKotlinLocalDate(),
            comportamiento = this.comportamiento,
            tecnica = this.tecnica,
            tactica = this.tactica,
            observaciones = this.observaciones
        )
    }
}

@Serializable
data class EvaluacionesDTO(
    val id: Int,
    val idJugador: Int,
    val idTemporada: Int?,
    @Contextual val fecha: LocalDate?,
    val comportamiento: Int,
    val tecnica: Int,
    val tactica: Int,
    val observaciones: String?
)

@Serializable
data class CrearEvaluacionRequest(
    val idJugador: Int,
    val fecha: String,
    val comportamiento: Int,
    val tecnica: Int,
    val tactica: Int,
    val observaciones: String
)