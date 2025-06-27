package services

import model.AlineacionEquipoCuartoDAO
import model.AlineacionEquipoCuarto
import model.CuartosPartido
import model.Jugadores
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class AlineacionEquipoCuartoService {
    fun getAlineacionByCuarto(idCuarto: Int): List<AlineacionEquipoCuartoDAO> = transaction {
        AlineacionEquipoCuartoDAO.find { AlineacionEquipoCuarto.idCuarto eq idCuarto }.toList()
    }

    fun createAlineacionJugador(
        idCuarto: Int,
        idJugador: Int,
        posX: Float,
        posY: Float
    ): AlineacionEquipoCuartoDAO = transaction {
        AlineacionEquipoCuartoDAO.new {
            this.idCuarto = EntityID(idCuarto, CuartosPartido)
            this.idJugador = EntityID(idJugador, Jugadores)
            this.posX = posX
            this.posY = posY
        }
    }

    fun deleteAlineacionJugador(id: Int): Boolean = transaction {
        val alineacion = AlineacionEquipoCuartoDAO.findById(id) ?: return@transaction false
        alineacion.delete()
        true
    }
}