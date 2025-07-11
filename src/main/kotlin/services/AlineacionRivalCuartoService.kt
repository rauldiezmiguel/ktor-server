package services

import model.AlineacionEquipoCuartoDAO
import model.AlineacionEquipoDTO
import model.AlineacionRivalCuarto
import model.AlineacionRivalCuartoDAO
import model.AlineacionRivalDTO
import model.CuartosEquipo
import model.CuartosRival
import model.Jugadores
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class AlineacionRivalCuartoService {
    fun getAlineacionRivalByCuarto(idCuarto: Int): List<AlineacionRivalCuartoDAO> = transaction {
        AlineacionRivalCuartoDAO.find { AlineacionRivalCuarto.idCuarto eq EntityID(idCuarto, CuartosEquipo) }.toList()
    }

    fun createAlineacionRival(
        idCuarto: Int,
    ): AlineacionRivalCuartoDAO = transaction {
        AlineacionRivalCuartoDAO.new {
            this.idCuarto = EntityID(idCuarto, CuartosRival)
        }
    }

    fun actualizarPlayerAlineacionRival(
        id: Int,
        posX: Float?,
        posY: Float?
    ): AlineacionRivalCuartoDAO? = transaction {
        val alineacion = AlineacionRivalCuartoDAO.findById(id) ?: return@transaction null

        alineacion.posX = posX ?: 0f
        alineacion.posY = posY ?: 0f

        alineacion
    }

    fun deleteAlineacionRival(id: Int): Boolean = transaction {
        val alineacion = AlineacionRivalCuartoDAO.findById(id) ?: return@transaction false
        alineacion.delete()
        true
    }
}