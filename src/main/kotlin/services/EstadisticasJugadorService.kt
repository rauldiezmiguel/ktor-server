package services

import model.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class EstadisticasJugadorService {

    fun getEstadisticasByJugadorByPartido(idJugador: Int, idPartido: Int): List<EstadisticasJugadorDAO> = transaction {
        EstadisticasJugadorDAO.find { (EstadisticasJugador.idJugador eq idJugador) and (EstadisticasJugador.idPartido eq idPartido) }.toList()
    }

    fun createEstadisticasJugador(idJugador: Int, idPartido: Int, minutosJugados: Int, goles: Int, asistencias: Int, titular: Boolean, tarjetasAmarillas: Int, tarjetasRojas: Int, partidoJugado: Boolean): EstadisticasJugadorDAO = transaction {
        val temporadaId = requireTemporadaActivaId()

        EstadisticasJugadorDAO.new {
            this.idJugador = EntityID( idJugador, Jugadores)
            this.idPartido = EntityID( idPartido, Partidos)
            this.idTemporada = EntityID(temporadaId, Temporadas)
            this.minutosJugados = minutosJugados
            this.goles = goles
            this.asistencias = asistencias
            this.titular = titular
            this.tarjetasAmarillas = tarjetasAmarillas
            this.tarjetasRojas = tarjetasRojas
            this.partidoJugado = partidoJugado
        }
    }

    fun updateEstadisticasJugador(id: Int, idJugador: Int, idPartido: Int, idTemporada: Int, minutosJugados: Int, goles: Int, asistencias: Int, titular: Boolean, tarjetasAmarillas: Int, tarjetasRojas: Int, partidoJugado: Boolean): Boolean = transaction {
        val estadisticaJugador = EstadisticasJugadorDAO.findById(id) ?: return@transaction false

        idJugador.let { estadisticaJugador.idJugador = EntityID( it, Jugadores) }
        idPartido.let { estadisticaJugador.idPartido = EntityID( it, Partidos) }
        idTemporada.let { estadisticaJugador.idTemporada = EntityID( it, Temporadas) }
        minutosJugados.let { estadisticaJugador.minutosJugados = it }
        goles.let { estadisticaJugador.goles = it }
        asistencias.let { estadisticaJugador.asistencias = it }
        titular.let { estadisticaJugador.titular = it }
        tarjetasAmarillas.let { estadisticaJugador.tarjetasAmarillas = it }
        tarjetasRojas.let { estadisticaJugador.tarjetasRojas = it }
        partidoJugado.let { estadisticaJugador.partidoJugado = it }
        true
    }

    /**
     * Devuelve la estadística de un jugador en cada uno de los partidos
     * que su equipo jugó en una temporada, ordenados por fecha de partido.
     *
     * @param idJugador       Id del jugador cuya estadística queremos.
     * @param idEquipo        Id del equipo cuyos partidos queremos.
     * @param idTemporada     Id de la temporada.
     * @param nomEstadistica  Nombre de la estadística: "Goles", "Asistencias",
     *                        "Min. Jugados", "Tarjetas Amarillas" o "Tarjetas Rojas".
     * @return Lista de DTOs con (idPartido, nomEstadistica, valorEstadistica, nombreRival),
     *         ordenados cronológicamente por fecha de partido.
     */
    fun getDetalleEstadisticaJugador(
        idJugador: Int,
        idEquipo: Int,
        idTemporada: Int,
        nomEstadistica: String
    ): List<EstadisticaPartidoDTO> = transaction {
        // 1) Traer todos los partidos del equipo en esa temporada, ordenados por fecha ascendente
        val partidos = PartidosDAO.find {
            (Partidos.idEquipo eq idEquipo) and
                    (Partidos.idTemporada eq idTemporada) and
                    (Partidos.resultadoNumerico neq null) // Solo partidos con resultado
        }
            .orderBy(Partidos.fecha to SortOrder.ASC)
            .toList()

        // Normaliza el nombre de la estadística (minúsculas, sin espacios sobrantes)
        val key = nomEstadistica.trim().lowercase()

        // 2) Para cada partido, buscar la fila de estadística del jugador
        partidos.mapNotNull { partido ->
            val estadisticaJugador = EstadisticasJugadorDAO.find {
                (EstadisticasJugador.idPartido eq partido.id.value) and
                        (EstadisticasJugador.idJugador eq idJugador) and
                        (EstadisticasJugador.idTemporada eq idTemporada)
            }
                .firstOrNull() ?: return@mapNotNull null

            // 3) Seleccionar el valor según la estadística solicitada
            val valor: Int = when (key) {
                "goles" -> estadisticaJugador.goles
                "asistencias" -> estadisticaJugador.asistencias
                "min.jugados", "minutosjugados" -> estadisticaJugador.minutosJugados
                "tarjetasamarillas" -> estadisticaJugador.tarjetasAmarillas
                "tarjetasrojas" -> estadisticaJugador.tarjetasRojas
                "titularidades" -> if (estadisticaJugador.minutosJugados > 0 && estadisticaJugador.titular) 1 else 0
                "partidosjugados" -> if (estadisticaJugador.minutosJugados > 0) 1 else 0
                else -> return@mapNotNull null
            }

            // 4) Empaquetar en el DTO para consumo desde el ViewModel/UI
            EstadisticaPartidoDTO(
                idPartido = partido.id.value,
                nomEstadistica = nomEstadistica,
                valorEstadistica = valor,
                nombreRival = partido.nombreRival
            )
        }
    }

    private fun getTemporadaActivaId(): Int? = transaction {
        TemporadaDAO.find { Temporadas.activa eq true }
            .maxByOrNull { it.añoInicio }
            ?.id?.value
    }

    private fun requireTemporadaActivaId(): Int = getTemporadaActivaId()
        ?: error("No hay temporada activa configurada")
}