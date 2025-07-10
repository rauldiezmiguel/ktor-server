package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

// Tabla cuartos_rival
object CuartosRival : IntIdTable("cuartos_rival") {
    val idPartido = reference("id_partido", Partidos, onDelete = ReferenceOption.CASCADE)
    val numero = integer("numero") // 1 - 4
    val analisisRival = text("analisis_rival").nullable()
    val observaciones = text("observaciones").nullable()

    init {
        uniqueIndex(idPartido, numero)
    }
}

// DAO cuartos_rival
class CuartosRivalDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CuartosRivalDAO>(CuartosRival)

    var idPartido by CuartosRival.idPartido
    var numero by CuartosRival.numero
    var analisisRival by CuartosRival.analisisRival
    var observaciones by CuartosRival.observaciones

    fun toDTO(): CuartosRivalDTO {
        return CuartosRivalDTO(
            id = this.id.value,
            idPartido = this.idPartido.value,
            numero = this.numero,
            analisisRival = this.analisisRival,
            observaciones = this.observaciones
        )
    }

    fun toDTOPartido(idAlineacion: Int): CuartosRivalCrearPartidoDTO {
        return CuartosRivalCrearPartidoDTO(
            id = this.id.value,
            idPartido = this.idPartido.value,
            numero = this.numero,
            idAlineacion = idAlineacion,
            analisisRival = this.analisisRival,
            observaciones = this.observaciones
        )
    }
}

// DTO cuartos_rival
@Serializable
data class CuartosRivalDTO(
    val id: Int,
    val idPartido: Int,
    val numero: Int,
    val analisisRival: String?,
    val observaciones: String?
)

@Serializable
data class CuartosRivalCrearPartidoDTO(
    val id: Int,
    val idPartido: Int,
    val numero: Int,
    val idAlineacion: Int,
    val analisisRival: String?,
    val observaciones: String?
)

// Request para modificar cuartos_rival
@Serializable
data class ModificarCuartosRivalRequest(
    val analisisRival: String?,
    val observaciones: String?
)

// Request para crear cuarto_rival
@Serializable
data class CrearCuartoRivalRequest(
    val idPartido: Int,
    val numero: Int
)
