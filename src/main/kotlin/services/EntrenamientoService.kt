package services

import model.Entrenamientos
import model.EntrenamientosDAO
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import model.Temporadas

class EntrenamientoService {
    fun getEntrenamientosByEquipo(idEquipo: Int): List<EntrenamientosDAO> = transaction {
        EntrenamientosDAO.find { Entrenamientos.idEquipo eq idEquipo }.toList()
    }

    fun createEntrenamiento(fecha: LocalDate, descripcion: String, entrenamientoUrl: String, idEquipo: Int): EntrenamientosDAO = transaction {
        EntrenamientosDAO.new {
            this.fecha = fecha.toJavaLocalDate()
            this.descripcion = descripcion
            this.entrenamientoUrl = entrenamientoUrl
            this.idEquipo = EntityID(idEquipo, Entrenamientos)
            this.idTemporada = EntityID(1, Temporadas)
        }
    }

    fun deleteEntrenamientoById(id: Int): Boolean = transaction {
        val entrenamientoDelete = EntrenamientosDAO.findById(id) ?: return@transaction false
        entrenamientoDelete.delete()
        true
    }
}