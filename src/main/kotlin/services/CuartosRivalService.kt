package services

import model.AlineacionRivalCuarto
import model.AlineacionRivalCuartoDAO
import model.CuartosRival
import model.CuartosRivalCrearPartidoDTO
import model.CuartosRivalDAO
import model.CuartosRivalDTO
import model.Partidos
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class CuartosRivalService {

    fun getCuartosRivalByPartido(idPartido: Int): List<CuartosRivalCrearPartidoDTO> = transaction {
        CuartosRivalDAO.find { CuartosRival.idPartido eq EntityID(idPartido, Partidos) }
            .map { cuartoRival ->
                val alineacion = AlineacionRivalCuartoDAO.find { AlineacionRivalCuarto.idCuarto eq cuartoRival.id }
                    .firstOrNull()

                cuartoRival.toDTOPartido(alineacion?.id?.value ?: -1)
            }
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
