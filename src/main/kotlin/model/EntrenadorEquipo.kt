package model

import kotlinx.serialization.Serializable
import model.Equipos.nullable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object EntrenadorEquipo : IntIdTable("entrenador_equipo") {
    val idEntrenador = reference("id_entrenador", Usuarios, onDelete = ReferenceOption.CASCADE)
    val idEquipo = reference("id_equipo", Equipos, onDelete = ReferenceOption.CASCADE)
    val idTemporada = reference("id_temporada", Temporadas, onDelete = ReferenceOption.CASCADE)
    init {
        uniqueIndex(idEntrenador, idEquipo) // Evita duplicados
    }
}

class EntrenadorEquipoDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EntrenadorEquipoDAO>(EntrenadorEquipo)

    var idEntrenador by EntrenadorEquipo.idEntrenador
    var idEquipo by EntrenadorEquipo.idEquipo
    var idTemporada by EntrenadorEquipo.idTemporada

    fun toDTO(): EntrenadorEquipoDTO {
        return EntrenadorEquipoDTO(
            id = this.id.value,
            idEntrenador = this.idEntrenador.value,
            idEquipo = this.idEquipo.value,
            idTemporada = this.idTemporada.value
        )
    }
}

@Serializable
data class EntrenadorEquipoDTO(
    val id: Int,
    val idEntrenador: Int,
    val idEquipo: Int,
    val idTemporada: Int?,
)

@Serializable
data class EntrenadorEquipoResponse(
    val idEquipo: Int
)