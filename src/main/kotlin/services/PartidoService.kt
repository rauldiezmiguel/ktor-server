package services

import org.jetbrains.exposed.sql.transactions.transaction
import model.*
import org.jetbrains.exposed.dao.id.EntityID
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate

class PartidoService {
    private val idTemporadas: Int = 1

    fun getPartidosByEquipo(idEquipo: Int): List<PartidosDAO> = transaction {
        PartidosDAO.find { Partidos.idEquipo eq idEquipo }.toList()
    }

    fun createPartido(idEquipo: Int, nombreRival: String, fecha: LocalDate): PartidosDAO = transaction {
        PartidosDAO.new {
            this.idEquipo = EntityID(idEquipo, Equipos)
            this.idTemporada = EntityID(idTemporadas, Temporadas)
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
}