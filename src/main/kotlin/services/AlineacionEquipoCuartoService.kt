package services

import model.AlineacionEquipoCuartoDAO
import model.AlineacionEquipoCuarto
import model.CuartosEquipo
import model.Jugadores
import model.PartidosDAO
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class AlineacionEquipoCuartoService {
    fun getAlineacionByCuarto(idCuarto: Int): List<AlineacionEquipoCuartoDAO> = transaction {
        AlineacionEquipoCuartoDAO.find { AlineacionEquipoCuarto.idCuarto eq EntityID(idCuarto, CuartosEquipo) }.toList()
    }

    fun addPlayerAlineacionJugador(
        id: Int,
        idJugador: Int,
        posX: Float?,
        posY: Float?
    ): AlineacionEquipoCuartoDAO? = transaction {
        val alineacion = AlineacionEquipoCuartoDAO.findById(id) ?: return@transaction null

        alineacion.idJugador = EntityID(idJugador, Jugadores)
        alineacion.posX = posX ?: 0f
        alineacion.posY = posY ?: 0f

        alineacion
    }

    fun createAlineacionEquipoCuarto(
        idCuarto: Int
    ): AlineacionEquipoCuartoDAO = transaction {
        AlineacionEquipoCuartoDAO.new {
            this.idCuarto = EntityID(idCuarto, CuartosEquipo)
        }
    }

    fun deleteAlineacionJugador(id: Int): Boolean = transaction {
        val alineacion = AlineacionEquipoCuartoDAO.findById(id) ?: return@transaction false
        alineacion.delete()
        true
    }
}