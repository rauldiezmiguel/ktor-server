package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object CuartosPartido : IntIdTable("cuartos_partido") {
    val idPartido = reference("id_partido", Partidos, onDelete = ReferenceOption.CASCADE)
    val numero = integer("numero") // 1 - 4
    val funcionamiento = text("funcionamiento").nullable()
    val danoRival = text("dano_rival").nullable()
    val observaciones = text("observaciones").nullable()
    val analisisRival = text("analisis_rival").nullable()
    val observacionesRival = text("observaciones_rival").nullable()

    init {
        uniqueIndex(idPartido, numero)
    }
}

class CuartosPartidoDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CuartosPartidoDAO>(CuartosPartido)

    var idPartido by CuartosPartido.idPartido
    var numero by CuartosPartido.numero
    var funcionamiento by CuartosPartido.funcionamiento
    var danoRival by CuartosPartido.danoRival
    var observaciones by CuartosPartido.observaciones
    var analisisRival by CuartosPartido.analisisRival
    var observacionesRival by CuartosPartido.observacionesRival

    fun toDTO(): CuartosPartidoDTO {
        return CuartosPartidoDTO(
            id = this.id.value,
            idPartido = this.idPartido.value,
            numero = this.numero,
            funcionamiento = this.funcionamiento,
            danoRival = this.danoRival,
            observaciones = this.observaciones,
            analisisRival = this.analisisRival,
            observacionesRival = this.observacionesRival
        )
    }
}

@Serializable
data class CuartosPartidoDTO(
    val id: Int,
    val idPartido: Int,
    val numero: Int,
    val funcionamiento: String?,
    val danoRival: String?,
    val observaciones: String?,
    val analisisRival: String?,
    val observacionesRival: String?
)

@Serializable
data class ModificarCuartosEquipoRequest(
    val funcionamiento: String?,
    val danoRival: String?,
    val observaciones: String?
)

@Serializable
data class ModificarCuartosRivalRequest(
    val analisisRival: String?,
    val observacionesRival: String?
)

@Serializable
data class CrearCuartosPartidoRequest(
    val idPartido: Int,
    val numero: Int
)