package services

import org.jetbrains.exposed.sql.transactions.transaction
import model.*
import org.jetbrains.exposed.dao.id.EntityID
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import org.jetbrains.exposed.sql.and

class PartidoService {

    fun getPartidosByEquipo(idEquipo: Int): List<PartidosDAO> = transaction {
        val temporadaActivaId = getTemporadaActivaId() ?: return@transaction emptyList()

        PartidosDAO.find { (Partidos.idEquipo eq idEquipo) and (Partidos.idTemporada eq temporadaActivaId) }.toList()
    }

    fun createPartido(idEquipo: Int, nombreRival: String, fecha: LocalDate): PartidosDAO = transaction {
        val temporadaId = requireTemporadaActivaId()

        PartidosDAO.new {
            this.idEquipo = EntityID(idEquipo, Equipos)
            this.idTemporada = EntityID(temporadaId, Temporadas)
            this.nombreRival = nombreRival
            this.fecha = fecha.toJavaLocalDate()
        }
    }

    fun updatePartido(id: Int, resultadoNumerico: String, resultado: String, alineacion: String, analisisGeneral: String, jugadoresDestacados: String): PartidosDAO? = transaction {
        val partido = PartidosDAO.findById(id) ?: return@transaction null

        partido.resultadoNumerico = resultadoNumerico
        partido.resultado = resultado
        partido.alineacion = alineacion
        partido.analisisGeneral = analisisGeneral
        partido.jugadoresDestacados = jugadoresDestacados

        partido
    }

    fun deletePartido(id: Int): Boolean = transaction {
        val partidoDelete = PartidosDAO.findById(id) ?: return@transaction false
        partidoDelete.delete()
        true
    }

    private fun getTemporadaActivaId(): Int? = transaction {
        TemporadaDAO.find { Temporadas.activa eq true }
            .maxByOrNull { it.añoInicio }
            ?.id?.value
    }

    private fun requireTemporadaActivaId(): Int = getTemporadaActivaId()
        ?: error("No hay temporada activa configurada")
}