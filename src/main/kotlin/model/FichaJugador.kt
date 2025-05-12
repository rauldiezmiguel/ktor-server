package model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction

object FichaJugador : IntIdTable("ficha_jugador") {
    val idJugador = reference("id_jugador", Jugadores, onDelete = ReferenceOption.CASCADE)
    val idTemporada = reference("id_temporada", Temporadas, onDelete = ReferenceOption.CASCADE)
    val idEquipo = reference("id_equipo", Equipos, onDelete = ReferenceOption.CASCADE)
    val piernaHabil = text("pierna_habil").nullable()
    val caracteristicasFisicas = text("caracteristicas_fisicas").nullable()
    val caracteristicasTacticas = text("caracteristicas_tacticas").nullable()
    val caracteristicasTecnicas = text("caracteristicas_tecnicas").nullable()
    val conductaEntrenamiento = text("conducta_entrenamiento").nullable()
    val conductaConCompañeros = text("conducta_con_companeros").nullable()
}

class FichaJugadorDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FichaJugadorDAO>(FichaJugador)

    var idJugador by FichaJugador.idJugador
    var idTemporada by FichaJugador.idTemporada
    var idEquipo by FichaJugador.idEquipo
    var piernaHabil by FichaJugador.piernaHabil
    var caracteristicasFisicas by FichaJugador.caracteristicasFisicas
    var caracteristicasTacticas by FichaJugador.caracteristicasTacticas
    var caracteristicasTecnicas by FichaJugador.caracteristicasTecnicas
    var conductaEntrenamiento by FichaJugador.conductaEntrenamiento
    var conductaConCompañeros by FichaJugador.conductaConCompañeros

    val jugador by JugadorDAO referencedOn FichaJugador.idJugador

    fun toDTO(): FichaJugadorDTO {
        return FichaJugadorDTO(
            id = this.id.value,
            idJugador = this.idJugador.value,
            idTemporada = this.idTemporada.value,
            idEquipo = this.idEquipo.value,
            piernaHabil = this.piernaHabil,
            caracteristicasFisicas = this.caracteristicasFisicas,
            caracteristicasTacticas = this.caracteristicasTacticas,
            caracteristicasTecnicas = this.caracteristicasTecnicas,
            conductaEntrenamiento = this.conductaEntrenamiento,
            conductaConCompañeros = this.conductaConCompañeros,
            nombreJugador = this.jugador.nombreJugador
        )
    }
}

@Serializable
data class FichaJugadorDTO(
    val id: Int,
    val idJugador: Int,
    val idTemporada: Int,
    val idEquipo: Int,
    val piernaHabil: String?,
    val caracteristicasFisicas: String?,
    val caracteristicasTacticas: String?,
    val caracteristicasTecnicas: String?,
    val conductaEntrenamiento: String?,
    val conductaConCompañeros: String?,
    val nombreJugador: String? = null
)

@Serializable
data class FichaJugadorRequest(
    val idJugador: Int,
    val idEquipo: Int,
    val piernaHabil: String?,
    val caracteristicasFisicas: String?,
    val caracteristicasTacticas: String?,
    val caracteristicasTecnicas: String?,
    val conductaEntrenamiento: String?,
    val conductaConCompañeros: String?
)