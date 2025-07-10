package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

// Tabla cuartos_equipo
object CuartosEquipo : IntIdTable("cuartos_equipo") {
    val idPartido = reference("id_partido", Partidos, onDelete = ReferenceOption.CASCADE)
    val numero = integer("numero") // 1 - 4
    val funcionamiento = text("funcionamiento").nullable()
    val danoRival = text("dano_rival").nullable()
    val observaciones = text("observaciones").nullable()

    init {
        uniqueIndex(idPartido, numero)
    }
}

// DAO cuartos_equipo
class CuartosEquipoDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CuartosEquipoDAO>(CuartosEquipo)

    var idPartido by CuartosEquipo.idPartido
    var numero by CuartosEquipo.numero
    var funcionamiento by CuartosEquipo.funcionamiento
    var danoRival by CuartosEquipo.danoRival
    var observaciones by CuartosEquipo.observaciones

    fun toDTO(idAlineacion: Int): CuartosEquipoDTO {
        return CuartosEquipoDTO(
            id = this.id.value,
            idPartido = this.idPartido.value,
            numero = this.numero,
            idAlineacion = idAlineacion,
            funcionamiento = this.funcionamiento,
            danoRival = this.danoRival,
            observaciones = this.observaciones
        )
    }
}

// DTO cuartos_equipo
@Serializable
data class CuartosEquipoDTO(
    val id: Int,
    val idPartido: Int,
    val numero: Int,
    val idAlineacion: Int,
    val funcionamiento: String?,
    val danoRival: String?,
    val observaciones: String?
)

// Request para modificar cuartos_equipo
@Serializable
data class ModificarCuartosEquipoRequest(
    val funcionamiento: String?,
    val danoRival: String?,
    val observaciones: String?
)

// Request para crear cuarto_equipo
@Serializable
data class CrearCuartoEquipoRequest(
    val idPartido: Int,
    val numero: Int
)
