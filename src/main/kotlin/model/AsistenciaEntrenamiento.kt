package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate

object AsistenciasEntrenamiento : IntIdTable("asistencia_entrenamiento") {
    val entrenamiento = reference("id_entrenamiento", Entrenamientos, onDelete = ReferenceOption.CASCADE)
    val jugador = reference("id_jugador", Jugadores,     onDelete = ReferenceOption.CASCADE)
    val asistio = bool("asistio").default(true)
    val motivoInasistencia = text("motivo_inasistencia").nullable()

    init {
        uniqueIndex(entrenamiento, jugador)
    }
}

class AsistenciaEntrenamientoDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AsistenciaEntrenamientoDAO>(AsistenciasEntrenamiento)

    var entrenamiento by EntrenamientosDAO referencedOn AsistenciasEntrenamiento.entrenamiento
    var jugador       by JugadorDAO referencedOn AsistenciasEntrenamiento.jugador
    var asistio       by AsistenciasEntrenamiento.asistio
    var motivoInasistencia by AsistenciasEntrenamiento.motivoInasistencia

    fun toDTO() = AsistenciaEntrenamientoDTO(
        id                = id.value,
        idEntrenamiento   = entrenamiento.id.value,
        idJugador         = jugador.id.value,
        nombreJugador = jugador.nombreJugador,
        asistio           = asistio,
        fecha = entrenamiento.fecha.toKotlinLocalDate(),
        motivoInasistencia = motivoInasistencia
    )
}

@Serializable
data class AsistenciaEntrenamientoDTO(
    val id: Int,
    val idEntrenamiento: Int,
    val idJugador: Int,
    val nombreJugador: String,
    val asistio: Boolean,
    val fecha: LocalDate?,
    val motivoInasistencia: String?
)