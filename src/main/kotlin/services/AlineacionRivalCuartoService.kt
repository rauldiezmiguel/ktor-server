package services

import model.AlineacionRivalCuarto
import model.AlineacionRivalCuartoDAO
import model.CuartosPartido
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class AlineacionRivalCuartoService {
    fun getAlineacionRivalByCuarto(idCuarto: Int): List<AlineacionRivalCuartoDAO> = transaction {
        AlineacionRivalCuartoDAO.find { AlineacionRivalCuarto.idCuarto eq idCuarto }.toList()
    }

    fun createAlineacionRival(
        idCuarto: Int,
        dorsalJugador: Int,
        posX: Float,
        posY: Float
    ): AlineacionRivalCuartoDAO = transaction {
        AlineacionRivalCuartoDAO.new {
            this.idCuarto = EntityID(idCuarto, CuartosPartido)
            this.dorsalJugador = dorsalJugador
            this.posX = posX
            this.posY = posY
        }
    }

    fun deleteAlineacionRival(id: Int): Boolean = transaction {
        val alineacion = AlineacionRivalCuartoDAO.findById(id) ?: return@transaction false
        alineacion.delete()
        true
    }
}