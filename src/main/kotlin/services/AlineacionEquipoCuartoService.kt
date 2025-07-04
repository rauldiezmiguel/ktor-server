package services

import model.AlineacionEquipoCuartoDAO
import model.AlineacionEquipoCuarto
import model.CuartosPartido
import model.Jugadores
import model.PartidosDAO
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class AlineacionEquipoCuartoService {
    fun getAlineacionByCuarto(idCuarto: Int): List<AlineacionEquipoCuartoDAO> = transaction {
        AlineacionEquipoCuartoDAO.find { AlineacionEquipoCuarto.idCuarto eq idCuarto }.toList()
    }

    fun addPlayerAlineacionJugador(
        id: Int,
        idJugador: Int,
        posX: Float,
        posY: Float
    ): AlineacionEquipoCuartoDAO? = transaction {
        val alineacion = AlineacionEquipoCuartoDAO.findById(id) ?: return@transaction null

        alineacion.idJugador = EntityID(idJugador, Jugadores)
        alineacion.posX = posX
        alineacion.posY = posY

        alineacion
    }

    fun createAlineacionEquipoCuarto(
        idCuarto: Int
    ): AlineacionEquipoCuartoDAO = transaction {
        AlineacionEquipoCuartoDAO.new {
            this.idCuarto = EntityID(idCuarto, CuartosPartido)
        }
    }

    fun deleteAlineacionJugador(id: Int): Boolean = transaction {
        val alineacion = AlineacionEquipoCuartoDAO.findById(id) ?: return@transaction false
        alineacion.delete()
        true
    }
}