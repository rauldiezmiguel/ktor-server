package services

import org.jetbrains.exposed.sql.transactions.transaction
import model.*
import org.jetbrains.exposed.dao.id.EntityID
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import org.jetbrains.exposed.sql.and

class PartidoService {

    fun getPartidosByEquipo(idEquipo: Int): List<PartidosDAO> = transaction {
        PartidosDAO.find { (Partidos.idEquipo eq idEquipo) }.toList()
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

    fun crearPartidoConCuartos(
        idEquipo: Int,
        nombreRival: String,
        fecha: LocalDate
    ): PartidosDTO = transaction {
        val temporadaId = requireTemporadaActivaId()

        val equipo = EquipoDAO.findById(idEquipo)
            ?: throw IllegalArgumentException("Equipo no encontrado")

        val partido = PartidosDAO.new {
            this.idEquipo = EntityID(idEquipo, Equipos)
            this.idTemporada = EntityID(temporadaId, Temporadas)
            this.nombreRival = nombreRival
            this.fecha = fecha.toJavaLocalDate()
        }

        val numeroDeCuartos = when (equipo.categoria.lowercase()) {
            "futbol 7" -> 4
            "futbol 11" -> 2
            else -> throw IllegalArgumentException("Categoría de equipo inválida: ${equipo.categoria}")
        }

        val cuartosEquipo = (1..numeroDeCuartos).map { numero ->
            val cuartoEquipo = CuartosEquipoDAO.new {
                this.idPartido = partido.id
                this.numero = numero
            }

            AlineacionEquipoCuartoDAO.new {
                this.idCuarto = cuartoEquipo.id
            }

            cuartoEquipo.toDTO()
        }

        val cuartosRival = (1..numeroDeCuartos).map { numero ->
            val cuartoRival = CuartosRivalDAO.new {
                this.idPartido = partido.id
                this.numero = numero
            }

            AlineacionRivalCuartoDAO.new {
                this.idCuarto = cuartoRival.id
            }

            cuartoRival.toDTO()
        }

        partido.toDTO(
            cuartosEquipoDTO = cuartosEquipo,
            cuartosRivalDTO = cuartosRival
        )
    }



    fun updatePartido(id: Int, resultadoNumerico: String, resultado: String, jugadoresDestacados: String): PartidosDAO? = transaction {
        val partido = PartidosDAO.findById(id) ?: return@transaction null

        partido.resultadoNumerico = resultadoNumerico
        partido.resultado = resultado
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