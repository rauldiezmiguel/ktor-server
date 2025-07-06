package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object AlineacionRivalCuarto : IntIdTable("alineacion_rival_cuarto") {
    val idCuarto = reference("id_cuarto", CuartosRival, onDelete = ReferenceOption.CASCADE)
    val dorsalJugador = integer("dorsal_jugador")
    val posX = float("pos_x")
    val posY = float("pos_y")
}

class AlineacionRivalCuartoDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AlineacionRivalCuartoDAO>(AlineacionRivalCuarto)

    var idCuarto by AlineacionRivalCuarto.idCuarto
    var dorsalJugador by AlineacionRivalCuarto.dorsalJugador
    var posX by AlineacionRivalCuarto.posX
    var posY by AlineacionRivalCuarto.posY

    fun toDTO(): AlineacionRivalDTO{
        return AlineacionRivalDTO(
            id = this.id.value,
            idCuarto = this.idCuarto.value,
            dorsalJugador = this.dorsalJugador,
            posX = this.posX,
            posY = this.posY
        )
    }
}

@Serializable
data class AlineacionRivalDTO(
    val id: Int,
    val idCuarto: Int,
    val dorsalJugador: Int,
    val posX: Float,
    val posY: Float
)

@Serializable
data class CrearAlineacionRivalRequest(
    val idCuarto: Int
)

@Serializable
data class AddPlayerAlineacionRivalRequest(
    val idCuarto: Int,
    val dorsalJugador: Int,
    val posX: Float,
    val posY: Float
)
