package services

import model.CuartosRival
import model.CuartosRivalDAO
import model.Partidos
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class CuartosRivalService {

    fun getCuartosByPartido(idPartido: Int): List<CuartosRivalDAO> = transaction {
        CuartosRivalDAO.find { CuartosRival.idPartido eq idPartido }.toList()
    }

    fun createCuarto(idPartido: Int, numero: Int): CuartosRivalDAO = transaction {
        CuartosRivalDAO.new {
            this.idPartido = EntityID(idPartido, Partidos)
            this.numero = numero
        }
    }

    fun updateCuarto(
        id: Int,
        analisisRival: String?,
        observaciones: String?
    ): CuartosRivalDAO? = transaction {
        val cuarto = CuartosRivalDAO.findById(id) ?: return@transaction null

        cuarto.analisisRival = analisisRival
        cuarto.observaciones = observaciones

        cuarto
    }

    fun deleteCuarto(id: Int): Boolean = transaction {
        val cuarto = CuartosRivalDAO.findById(id) ?: return@transaction false
        cuarto.delete()
        true
    }
}
