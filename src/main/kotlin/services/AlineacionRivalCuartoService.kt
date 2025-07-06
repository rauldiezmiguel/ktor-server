package services

import model.AlineacionEquipoCuartoDAO
import model.AlineacionRivalCuarto
import model.AlineacionRivalCuartoDAO
import model.CuartosRival
import model.Jugadores
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class AlineacionRivalCuartoService {
    fun getAlineacionRivalByCuarto(idCuarto: Int): List<AlineacionRivalCuartoDAO> = transaction {
        AlineacionRivalCuartoDAO.find { AlineacionRivalCuarto.idCuarto eq idCuarto }.toList()
    }

    fun createAlineacionRival(
        idCuarto: Int,
    ): AlineacionRivalCuartoDAO = transaction {
        AlineacionRivalCuartoDAO.new {
            this.idCuarto = EntityID(idCuarto, CuartosRival)
        }
    }

    fun addPlayerAlineacionRival(
        id: Int,
        dorsalJugador: Int,
        posX: Float,
        posY: Float
    ): AlineacionRivalCuartoDAO? = transaction {
        val alineacion = AlineacionRivalCuartoDAO.findById(id) ?: return@transaction null

        alineacion.dorsalJugador = dorsalJugador
        alineacion.posX = posX
        alineacion.posY = posY

        alineacion
    }

    fun deleteAlineacionRival(id: Int): Boolean = transaction {
        val alineacion = AlineacionRivalCuartoDAO.findById(id) ?: return@transaction false
        alineacion.delete()
        true
    }
}