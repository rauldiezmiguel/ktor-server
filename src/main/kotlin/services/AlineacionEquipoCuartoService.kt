package services

import model.AlineacionEquipoCuartoDAO
import model.AlineacionEquipoCuarto
import model.AlineacionEquipoDTO
import model.CuartosEquipo
import model.Jugadores
import model.PartidosDAO
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import javax.swing.text.html.parser.Entity

class AlineacionEquipoCuartoService {
    fun getAlineacionByCuarto(idCuarto: Int): List<AlineacionEquipoCuartoDAO> = transaction {
        AlineacionEquipoCuartoDAO.find { AlineacionEquipoCuarto.idCuarto eq EntityID(idCuarto, CuartosEquipo) }.toList()
    }

    fun actualizarPlayerAlineacionJugador(
        id: Int,
        posX: Float?,
        posY: Float?
    ): AlineacionEquipoCuartoDAO? = transaction {
        val alineacion = AlineacionEquipoCuartoDAO.findById(id) ?: return@transaction null

        alineacion.posX = posX ?: 0f
        alineacion.posY = posY ?: 0f

        alineacion
    }

    fun createAlineacionEquipoCuarto(
        idCuarto: Int,
        idJugador: Int,
        posX: Float?,
        posY: Float?,
    ): AlineacionEquipoCuartoDAO = transaction {
        AlineacionEquipoCuartoDAO.new {
            this.idCuarto = EntityID(idCuarto, CuartosEquipo)
            this.idJugador = EntityID(idJugador, Jugadores)
            this.posX = posX ?: 0f
            this.posY = posY ?: 0f
        }
    }

    fun deleteAlineacionJugador(id: Int): Boolean = transaction {
        val alineacion = AlineacionEquipoCuartoDAO.findById(id) ?: return@transaction false
        alineacion.delete()
        true
    }
}