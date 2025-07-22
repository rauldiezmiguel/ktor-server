package services

import model.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and

class EquipoService {
    fun getEquiposByClub(idClub: Int): List<EquipoDAO> = transaction {
        val temporadaActivaId = getTemporadaActivaId() ?: return@transaction emptyList()
        EquipoDAO.find { (Equipos.idClub eq idClub) eq (Equipos.idTemporada eq temporadaActivaId) }.toList()
    }

    fun getEquipoById(id: Int): EquipoDAO? = transaction {
        EquipoDAO.findById(id)
    }

    fun createEquipo(nombreEquipo: String, categoria: String, subcategoria: String, idClub: Int, idTemporada: Int): EquipoDAO = transaction {
        EquipoDAO.new {
            this.nombreEquipo = nombreEquipo
            this.categoria = categoria
            this.subcategoria = subcategoria
            this.idClub = EntityID(idClub, Clubes)
            this.idTemporada = EntityID(idTemporada, Temporadas)
        }
    }

    fun updateEquipo(id: Int, nombre: String, categoria: String): Boolean = transaction {
        val equipo = EquipoDAO.findById(id) ?: return@transaction false

        nombre.let { equipo.nombreEquipo = it }
        categoria.let { equipo.categoria = it }
        true
    }

    fun deleteEquipo(id: Int): Boolean = transaction {
        val equipo = EquipoDAO.findById(id) ?: return@transaction false

        equipo.delete()
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