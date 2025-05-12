package model


import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import model.Equipos.nullable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.date
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toJavaLocalDate

object Entrenamientos : IntIdTable("entrenamientos") {
    val fecha = date("fecha")
    val descripcion = text("descripcion")
    val entrenamientoUrl = text("entrenamiento_url") // URL del archivo
    val idEquipo = reference("id_equipo", Equipos, onDelete = ReferenceOption.CASCADE) // FK a Equipos
    val idTemporada = reference("id_temporada", Temporadas, onDelete = ReferenceOption.CASCADE)
}

class EntrenamientosDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EntrenamientosDAO>(Entrenamientos)

    var fecha by Entrenamientos.fecha
    var descripcion by Entrenamientos.descripcion
    var entrenamientoUrl by Entrenamientos.entrenamientoUrl
    var idEquipo by Entrenamientos.idEquipo
    var idTemporada by Entrenamientos.idTemporada

    fun toDTO(): EntrenamientosDTO {
        return EntrenamientosDTO(
            id = this.id.value,
            fecha = this.fecha.toKotlinLocalDate(),
            descripcion = this.descripcion,
            entrenamientoUrl = this.entrenamientoUrl,
            idEquipo = this.idEquipo.value,
            idTemporada = this.idTemporada.value
        )
    }
}

@Serializable
data class EntrenamientosDTO(
    val id: Int,
    val fecha: LocalDate,
    val descripcion: String?,
    val entrenamientoUrl: String,
    val idEquipo: Int,
    val idTemporada: Int
)

@Serializable
data class CrearEntrenamientoRequest(
    val fecha: LocalDate,
    val descripcion: String,
    val entrenamientoUrl: String,
    val idEquipo: Int
)