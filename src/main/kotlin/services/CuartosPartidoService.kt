package services

import model.CuartosPartido
import model.CuartosPartidoDAO
import model.Partidos
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class CuartosPartidoService {
    fun getCuartosByPartido(idPartido: Int): List<CuartosPartidoDAO> = transaction {
        CuartosPartidoDAO.find { CuartosPartido.idPartido eq idPartido }.toList()
    }

    fun createCuarto(idPartido: Int, numero: Int): CuartosPartidoDAO = transaction {
        CuartosPartidoDAO.new {
            this.idPartido = EntityID(idPartido, Partidos)
            this.numero = numero
        }
    }

    fun updateCuartoEquipo(
        id: Int,
        funcionamiento: String?,
        danoRival: String?,
        observaciones: String?
    ): CuartosPartidoDAO? = transaction {
        val cuarto = CuartosPartidoDAO.findById(id) ?: return@transaction null

        cuarto.funcionamiento = funcionamiento
        cuarto.danoRival = danoRival
        cuarto.observaciones = observaciones

        cuarto
    }

    fun updateCuartoRival(
        id: Int,
        analisisRival: String?,
        observacionesRival: String?
    ): CuartosPartidoDAO? = transaction {
        val cuarto = CuartosPartidoDAO.findById(id) ?: return@transaction null

        cuarto.analisisRival = analisisRival
        cuarto.observacionesRival = observacionesRival

        cuarto
    }

    fun deleteCuarto(id: Int): Boolean = transaction {
        val cuarto = CuartosPartidoDAO.findById(id) ?: return@transaction false
        cuarto.delete()
        true
    }
}