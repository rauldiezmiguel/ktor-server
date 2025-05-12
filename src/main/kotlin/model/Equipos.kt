package model

import kotlinx.serialization.Serializable
import model.Usuarios.nombreUsuario
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Equipos : IntIdTable("equipos") {
    val nombreEquipo = text("nombre_equipo")
    val categoria = text("categoria").check { it inList listOf("fútbol 7", "fútbol 11") } // "fútbol 7" o "fútbol 11"
    val subcategoria = text("subcategoria").check { it inList listOf("sub7", "sub8", "sub9", "sub10", "sub11", "sub12", "sub13", "sub14", "sub15", "sub16", "juveniles") }
    val idClub = reference("id_club", Clubes, onDelete = ReferenceOption.CASCADE) // FK a Clubes
    val idTemporada = reference("id_temporada", Temporadas, onDelete = ReferenceOption.CASCADE)
}

class EquipoDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EquipoDAO>(Equipos)

    var nombreEquipo by Equipos.nombreEquipo
    var categoria by Equipos.categoria
    var subcategoria by Equipos.subcategoria
    var idClub by Equipos.idClub
    var idTemporada by Equipos.idTemporada

    fun toDTO(): EquipoDTO {
        return EquipoDTO(
            id = this.id.value,
            nombreEquipo = this.nombreEquipo,
            categoria = this.categoria,
            subcategoria = this.subcategoria,
            idClub = this.idClub.value,
            idTemporada = this.idTemporada.value
        )
    }
}

@Serializable
data class EquipoDTO(
    val id: Int,
    val nombreEquipo: String,
    val categoria: String,
    val subcategoria: String,
    val idClub: Int?,
    val idTemporada: Int?
)

