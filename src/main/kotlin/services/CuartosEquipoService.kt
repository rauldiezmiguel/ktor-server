package services

import model.AlineacionEquipoCuarto
import model.AlineacionEquipoCuartoDAO
import model.CuartosEquipo
import model.CuartosEquipoCrearPartidoDTO
import model.CuartosEquipoDAO
import model.CuartosEquipoDTO
import model.Partidos
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class CuartosEquipoService {

    fun getCuartosByPartido(idPartido: Int): List<CuartosEquipoCrearPartidoDTO> = transaction {
        CuartosEquipoDAO.find { CuartosEquipo.idPartido eq EntityID(idPartido, Partidos) }
            .map { cuartoEquipo ->
                // Obtener alineacion relacionada (suponiendo que tienes relación one-to-one)
                val alineacion = AlineacionEquipoCuartoDAO.find { AlineacionEquipoCuarto.idCuarto eq cuartoEquipo.id }
                    .firstOrNull()

                cuartoEquipo.toDTOPartido(alineacion?.id?.value ?: -1)  // Pasa el idAlineacion o -1 si no existe
            }
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
