package services

import kotlinx.datetime.toKotlinLocalDate
import model.AsistenciaEntrenamientoDAO
import model.AsistenciaEntrenamientoDTO
import model.AsistenciasEntrenamiento
import model.Entrenamientos
import model.EntrenamientosDAO
import model.JugadorDAO
import model.Jugadores
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class AsistenciaEntrenamientosService {
    /**
     * Devuelve todos los jugadores del equipo de ese entrenamiento,
     * con su flag de asistencia (true/false).
     */
    fun getAsistencias(entrenamientoId: Int): List<AsistenciaEntrenamientoDTO> = transaction {
        // 1) Recupera el entrenamiento para saber el equipo
        val entrenamiento = EntrenamientosDAO.findById(entrenamientoId)
            ?: throw IllegalArgumentException("Entrenamiento $entrenamientoId no existe")

        val idEquipo = entrenamiento.idEquipo.value

        // 2) Trae todos los jugadores de ese equipo
        val jugadores = JugadorDAO.find { Jugadores.idEquipo eq idEquipo }.toList()

        // 3) Para cada jugador, busca si hay asistencia en la tabla
        jugadores.map { jugador ->
            val existing = AsistenciaEntrenamientoDAO.find {
                (AsistenciasEntrenamiento.entrenamiento eq entrenamientoId) and
                        (AsistenciasEntrenamiento.jugador       eq jugador.id)
            }.firstOrNull()

            // Rellenamos el DTO completo, incluyendo el `id` de la fila si existe
            AsistenciaEntrenamientoDTO(
                id               = existing?.id?.value ?: 0,
                idEntrenamiento  = entrenamientoId,
                idJugador        = jugador.id.value,
                nombreJugador = jugador.nombreJugador,
                asistio          = existing?.asistio ?: false,
                fecha = entrenamiento.fecha.toKotlinLocalDate()
            )
        }
    }

    /** Marca asistencia para un jugador (inserta o actualiza) */
    fun marcarAsistencia(
        entrenamientoId: Int,
        jugadorId: Int,
        asistio: Boolean
    ) = transaction {
        val existing = AsistenciaEntrenamientoDAO.find {
            (AsistenciasEntrenamiento.entrenamiento eq entrenamientoId) and
                    (AsistenciasEntrenamiento.jugador       eq jugadorId)
        }.firstOrNull()

        if (existing != null) {
            existing.asistio = asistio
        } else {
            AsistenciaEntrenamientoDAO.new {
                entrenamiento = EntrenamientosDAO[entrenamientoId]
                jugador = JugadorDAO[jugadorId]
                this.asistio  = asistio
            }
        }
    }

    /**
     * Devuelve todos los entrenamientos del equipo al que pertenece el jugador,
     * con un flag de asistencia para ese jugador.
     */
    fun getAsistenciasPorJugador(idJugador: Int): List<AsistenciaEntrenamientoDTO> = transaction {
        // 1) Averigua el teamId del jugador
        val jugador = JugadorDAO.findById(idJugador)
            ?: throw IllegalArgumentException("Jugador $idJugador no existe")
        val teamId = jugador.idEquipo.value  // o .equipo.id.value según tu DAO

        // 2) Recupera entrenamientos de ese equipo, ordenados por fecha
        val entrenamientos = EntrenamientosDAO.find {
            Entrenamientos.idEquipo eq teamId
        }.orderBy(Entrenamientos.fecha to SortOrder.ASC).toList()

        // 3) Para cada entrenamiento, busca asistencia
        entrenamientos.map { entDao ->
            val existing = AsistenciaEntrenamientoDAO.find {
                (AsistenciasEntrenamiento.entrenamiento eq entDao.id.value) and
                        (AsistenciasEntrenamiento.jugador eq idJugador)
            }.firstOrNull()

            // Rellenamos el DTO completo, incluyendo el `id` de la fila si existe
            AsistenciaEntrenamientoDTO(
                id               = existing?.id?.value ?: 0,
                idEntrenamiento  = entDao.id.value,
                idJugador        = jugador.id.value,
                nombreJugador = jugador.nombreJugador,
                asistio          = existing?.asistio ?: false,
                fecha = entDao.fecha.toKotlinLocalDate()
            )
        }
    }
}