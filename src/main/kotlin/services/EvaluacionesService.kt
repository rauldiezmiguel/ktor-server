package services

import kotlinx.datetime.toKotlinLocalDate
import model.Evaluaciones
import model.EvaluacionesDAO
import model.Jugadores
import model.PromedioMensualDTO
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.and
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.datetime.toJavaLocalDate
import model.TemporadaDAO
import model.Temporadas

class EvaluacionesService {
    fun getEvaluacionesByJugador(idJugador: Int): List<EvaluacionesDAO> = transaction {
        EvaluacionesDAO.find { Evaluaciones.idJugador eq idJugador }.toList()
    }

    fun createEvaluaciones(
        idJugador: Int,
        fecha: LocalDate,
        comportamiento: Int,
        tecnica: Int,
        tactica: Int,
        observaciones: String
    ): EvaluacionesDAO = transaction {

        // Obtener el rango de la semana (lunes a domingo)
        val dayOfWeek = fecha.dayOfWeek.value // 1 = Lunes, 7 = Domingo
        val startOfWeek = fecha.minusDays((dayOfWeek - 1).toLong())
        val endOfWeek = fecha.plusDays((7 - dayOfWeek).toLong())
        val temporadaId = requireTemporadaActivaId()

        // Verificar si ya existe una evaluación esa semana
        val evaluacionExistente = EvaluacionesDAO.find {
            (Evaluaciones.idJugador eq idJugador) and
                    (Evaluaciones.fecha greaterEq startOfWeek) and
                    (Evaluaciones.fecha lessEq endOfWeek)
        }.firstOrNull()

        if (evaluacionExistente != null) {
            throw IllegalArgumentException("Ya existe una evaluación para este jugador en la semana del ${startOfWeek} al ${endOfWeek}")
        }

        // Crear la evaluación si no hay conflicto
        EvaluacionesDAO.new {
            this.idJugador = EntityID(idJugador, Jugadores)
            this.idTemporada = EntityID(temporadaId, Temporadas)
            this.fecha = fecha
            this.comportamiento = comportamiento
            this.tecnica = tecnica
            this.tactica = tactica
            this.observaciones = observaciones
        }
    }

    fun updateEvaluacion(id: Int, idJugador: Int, fecha: LocalDate, comportamiento: Int, tecnica: Int, tactica: Int, observaciones: String): Boolean = transaction {
        val evaluacion = EvaluacionesDAO.findById(id) ?: return@transaction false

        idJugador.let { evaluacion.idJugador = EntityID(it, Jugadores) }
        fecha.let { evaluacion.fecha = it }
        comportamiento.let { evaluacion.comportamiento = it }
        tecnica.let { evaluacion.tecnica = it }
        tactica.let { evaluacion.tactica = it }
        observaciones.let { evaluacion.observaciones = it }
        true
    }

    fun deleteEvaluacion(id: Int): Boolean = transaction {
        val evaluacion = EvaluacionesDAO.findById(id) ?: return@transaction false

        evaluacion.delete()
        true
    }

    fun getPromediosMensuales(idJugador: Int): List<PromedioMensualDTO> = transaction {
        val evaluaciones = EvaluacionesDAO.find { Evaluaciones.idJugador eq idJugador }
            .filter { it.fecha != null }

        evaluaciones
            .groupBy {
                val fechaJava = it.fecha!!.toKotlinLocalDate()
                YearMonth.of(fechaJava.year, fechaJava.monthNumber)
            }
            .map { (yearMonth, evals) ->
                PromedioMensualDTO(
                    año = yearMonth.year,
                    mes = yearMonth.monthValue,
                    comportamiento = evals.map { it.comportamiento }.average(),
                    tecnica = evals.map { it.tecnica }.average(),
                    tactica = evals.map { it.tactica }.average()
                )
            }
            .sortedBy { it.año * 100 + it.mes } // orden cronológico
    }

    private fun getTemporadaActivaId(): Int? = transaction {
        TemporadaDAO.find { Temporadas.activa eq true }
            .maxByOrNull { it.añoInicio }
            ?.id?.value
    }

    private fun requireTemporadaActivaId(): Int = getTemporadaActivaId()
        ?: error("No hay temporada activa")
}