package model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import model.Evaluaciones.nullable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.date
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.sql.Column

object Partidos : IntIdTable("partidos") {
    val idEquipo = reference("id_equipo", Equipos, onDelete = ReferenceOption.CASCADE)
    val idTemporada = reference("id_temporada", Temporadas, onDelete = ReferenceOption.CASCADE)
    val nombreRival = text("nombre_rival")
    val fecha = date("fecha")
    val resultadoNumerico = text("resultado_numerico").nullable()
    val resultado = text("resultado").nullable()
    val jugadoresDestacados = text("jugadores_destacados").nullable()
}

class PartidosDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PartidosDAO>(Partidos)

    var idEquipo by Partidos.idEquipo
    var idTemporada by Partidos.idTemporada
    var nombreRival by Partidos.nombreRival
    var fecha by Partidos.fecha
    var resultadoNumerico by Partidos.resultadoNumerico
    var resultado by Partidos.resultado
    var jugadoresDestacados by Partidos.jugadoresDestacados

    fun toDTO(): PartidosDTO {
        return PartidosDTO(
            id = this.id.value,
            idEquipo = this.idEquipo.value,
            idTemporada = this.idTemporada.value,
            nombreRival = this.nombreRival,
            fecha = this.fecha.toKotlinLocalDate(),
            resultadoNumerico = this.resultadoNumerico,
            resultado = this.resultado,
            jugadoresDestacados = this.jugadoresDestacados
        )
    }
}

@Serializable
data class PartidosDTO(
    val id: Int,
    val idEquipo: Int,
    val idTemporada: Int,
    val nombreRival: String,
    val fecha: LocalDate,
    val resultadoNumerico: String?,
    val resultado: String?,
    val jugadoresDestacados: String?
)

@Serializable
data class CrearPartidoRequest(
    val idEquipo: Int,
    val nombreRival: String,
    val fecha: LocalDate
)

@Serializable
data class ActualizarPartidoRequest(
    val resultadoNumerico: String,
    val resultado: String,
    val jugadoresDestacados: String
)
