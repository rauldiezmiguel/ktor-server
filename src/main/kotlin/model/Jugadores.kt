package model

import kotlinx.serialization.Serializable
import model.Equipos.nullable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Jugadores : IntIdTable("jugadores") {
    val nombreJugador = text("nombre")
    val dorsal = integer("dorsal").check { it.between(1, 99) }
    val posicion = text("posicion").nullable()
    val idEquipo = reference("id_equipo", Equipos, onDelete = ReferenceOption.CASCADE)
    val idTemporada = reference("id_temporada", Temporadas, onDelete = ReferenceOption.CASCADE).nullable()
}

class JugadorDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<JugadorDAO>(Jugadores)

    var nombreJugador by Jugadores.nombreJugador
    var dorsal by Jugadores.dorsal
    var posicion by Jugadores.posicion
    var idEquipo by Jugadores.idEquipo
    var idTemporada by Jugadores.idTemporada

    fun toDTO(): JugadorDTO {
        return JugadorDTO(
            id = this.id.value,
            nombreJugador = this.nombreJugador,
            dorsal = this.dorsal,
            posicion = this.posicion,
            idEquipo = idEquipo.value,
            idTemporada = idTemporada?.value
        )
    }
}

@Serializable
data class JugadorDTO(
    val id: Int,
    val nombreJugador: String,
    val dorsal: Int,
    val posicion: String?,
    val idEquipo: Int?,
    val idTemporada: Int?
)