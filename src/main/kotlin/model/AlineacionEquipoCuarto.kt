package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object AlineacionEquipoCuarto : IntIdTable("alineacion_equipo_cuarto") {
    val idCuarto = reference("id_cuarto", CuartosEquipo, onDelete = ReferenceOption.CASCADE)
    val idJugador = reference("id_jugador", Jugadores, onDelete = ReferenceOption.CASCADE)
    val posX = float("pos_x")
    val posY = float("pos_y")
}

class AlineacionEquipoCuartoDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AlineacionEquipoCuartoDAO>(AlineacionEquipoCuarto)

    var idCuarto by AlineacionEquipoCuarto.idCuarto
    var idJugador by AlineacionEquipoCuarto.idJugador
    var posX by AlineacionEquipoCuarto.posX
    var posY by AlineacionEquipoCuarto.posY

    fun toDTO(): AlineacionEquipoDTO{
        return AlineacionEquipoDTO(
            id = this.id.value,
            idCuarto = this.idCuarto.value,
            idJugador = this.idJugador.value,
            posX = this.posX,
            posY = this.posY
        )
    }
}

@Serializable
data class AlineacionEquipoDTO(
    val id: Int,
    val idCuarto: Int,
    val idJugador: Int,
    val posX: Float,
    val posY: Float
)

@Serializable
data class CrearAlineacionEquipoRequest(
    val idCuarto: Int
)

@Serializable
data class AddPlayerAlineacionEquipoRequest(
    val idCuarto: Int,
    val idJugador: Int,
    val posX: Float,
    val posY: Float
)