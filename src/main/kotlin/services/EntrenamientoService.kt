package services

import model.Entrenamientos
import model.EntrenamientosDAO
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import model.TemporadaDAO
import model.Temporadas

class EntrenamientoService {
    fun getEntrenamientosByEquipo(idEquipo: Int): List<EntrenamientosDAO> = transaction {
        val temporadaActivaId = getTemporadaActivaId() ?: return@transaction emptyList()
        EntrenamientosDAO.find { (Entrenamientos.idEquipo eq idEquipo) eq (Entrenamientos.idTemporada eq temporadaActivaId) }.toList()
    }

    fun createEntrenamiento(fecha: LocalDate, descripcion: String, entrenamientoUrl: String, idEquipo: Int): EntrenamientosDAO = transaction {
        val temporadaId = requireTemporadaActivaId()

        EntrenamientosDAO.new {
            this.fecha = fecha.toJavaLocalDate()
            this.descripcion = descripcion
            this.entrenamientoUrl = entrenamientoUrl
            this.idEquipo = EntityID(idEquipo, Entrenamientos)
            this.idTemporada = EntityID(temporadaId, Temporadas)
        }
    }

    fun deleteEntrenamientoById(id: Int): Boolean = transaction {
        val entrenamientoDelete = EntrenamientosDAO.findById(id) ?: return@transaction false
        entrenamientoDelete.delete()
        true
    }

    private fun getTemporadaActivaId(): Int? = transaction {
        TemporadaDAO.find { Temporadas.activa eq true }
            .maxByOrNull { it.añoInicio }
            ?.id?.value
    }

    private fun requireTemporadaActivaId(): Int = getTemporadaActivaId()
        ?: error("No hay temporada activa configurada")
}