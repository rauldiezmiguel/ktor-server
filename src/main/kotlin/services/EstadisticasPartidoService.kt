package services

import model.EstadisticaPartidoDTO
import model.EstadisticasPartido
import model.EstadisticasPartidoDAO
import model.Partidos
import model.PartidosDAO
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class EstadisticasPartidoService {
    fun getEstadisticasPartidoById(idPartido: Int): List<EstadisticasPartidoDAO> = transaction {
        EstadisticasPartidoDAO.find { EstadisticasPartido.idPartido eq idPartido }.toList()
    }

    /**
     * Recorre todos los partidos de un equipo en una temporada y obtiene
     * el valor de `nombEstadistica` para cada uno.
     *
     * @param idEquipo         Id del equipo cuyos partidos queremos.
     * @param idTemporada      Id de la temporada.
     * @param nomEstadistica  Nombre de la estadística: "goles", "asistencias", "minutos",
     *                         "tarjetasAmarillas" o "tarjetasRojas".
     * @return Lista de DTOs con (idPartido, nombEstadistica, valorEstadistica),
     *         ordenados cronológicamente por fecha de partido.
     */
    fun getDetalleEstadisticaEquipo(
        idEquipo: Int?,
        idTemporada: Int?,
        nomEstadistica: String?
    ): List<EstadisticaPartidoDTO> = transaction {
        // 1) Traer todos los partidos del equipo en esa temporada, ordenados por fecha ascendente
        val partidos = PartidosDAO.find {
            (Partidos.idEquipo eq idEquipo) and (Partidos.idTemporada eq idTemporada)
        }
            .orderBy(Partidos.fecha to SortOrder.ASC)
            .toList()

        // 2) Para cada partido, invocar getEstadisticaPartidoById y recoger los no nulos
        partidos.mapNotNull { partido ->
            // getEstadisticaPartidoById viene definida en este mismo service
            getEstadisticaPartidoById(partido.id.value, nomEstadistica, partido.nombreRival)
        }
    }

    /**
     * Devuelve una estadística concreta (goles, asistencias, etc.) para un partido.
     * @param idPartido        Id del partido.
     * @param nomEstadistica  Nombre de la estadística: "goles", "asistencias", "minutos",
     *                         "tarjetasAmarillas" o "tarjetasRojas".
     * @return DTO con (idPartido, nombEstadistica, valorEstadistica) o null si no existe.
     */
    fun getEstadisticaPartidoById(
        idPartido: Int,
        nomEstadistica: String?,
        nombreRival: String?
    ): EstadisticaPartidoDTO? = transaction {
        // Obtener la fila de estadisticas del partido
        val dao = EstadisticasPartidoDAO.find {
            EstadisticasPartido.idPartido eq idPartido
        }.firstOrNull() ?: return@transaction null

        // Seleccionar el valor adecuado según el nombre
        val valor = when (nomEstadistica) {
            "Goles"             -> dao.golesTotalesPartido
            "Asistencias"       -> dao.asistenciasTotalesPartido
            "Min. Jugados"           -> dao.minutosTotalesPartido
            "Tarjetas Amarillas" -> dao.tarjetasAmarillasTotalesPartido
            "Tarjetas Rojas"     -> dao.tarjetasRojasTotalesPartido
            else                -> return@transaction null
        }

        // Empaquetar el resultado en un DTO
        EstadisticaPartidoDTO(
            idPartido = idPartido,
            nomEstadistica = nomEstadistica,
            valorEstadistica = valor,
            nombreRival = nombreRival
        )
    }
}