package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Clubes : IntIdTable("clubes") {
    val nombreClub = text("nombre").uniqueIndex()
    val direccion = text("direccion").nullable()
    val telefono = varchar("telefono", 20).nullable()

}

class ClubDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ClubDAO>(Clubes)

    var nombreClub by Clubes.nombreClub
    var direccion by Clubes.direccion
    var telefono by Clubes.telefono

    fun toDTO(): ClubDTO{
        return ClubDTO(
            id = this.id.value,
            nombreClub = this.nombreClub,
            direccion = this.direccion,
            telefono = this.telefono
        )
    }
}

@Serializable
data class ClubDTO(
    val id: Int,
    val nombreClub: String,
    val direccion: String?,
    val telefono: String?
)