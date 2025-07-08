package services

import model.CuartosEquipo
import model.CuartosEquipoDAO
import model.Partidos
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class CuartosEquipoService {

    fun getCuartosByPartido(idPartido: Int): List<CuartosEquipoDAO> = transaction {
        CuartosEquipoDAO.find { CuartosEquipo.idPartido eq EntityID(idPartido, Partidos) }.toList()
    }

    fun createCuarto(idPartido: Int, numero: Int): CuartosEquipoDAO = transaction {
        CuartosEquipoDAO.new {
            this.idPartido = EntityID(idPartido, Partidos)
            this.numero = numero
        }
    }

    fun updateCuarto(
        id: Int,
        funcionamiento: String?,
        danoRival: String?,
        observaciones: String?
    ): CuartosEquipoDAO? = transaction {
        val cuarto = CuartosEquipoDAO.findById(id) ?: return@transaction null

        cuarto.funcionamiento = funcionamiento
        cuarto.danoRival = danoRival
        cuarto.observaciones = observaciones

        cuarto
    }

    fun deleteCuarto(id: Int): Boolean = transaction {
        val cuarto = CuartosEquipoDAO.findById(id) ?: return@transaction false
        cuarto.delete()
        true
    }
}
