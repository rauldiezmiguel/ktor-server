package services

import model.*
import org.jetbrains.exposed.sql.transactions.transaction

class TemporadaService {
    fun getAllTemporadas(): List<TemporadaDAO> = transaction {
        TemporadaDAO.all().toList()
    }

    fun getTemporadaById(id: Int): TemporadaDAO? = transaction {
        TemporadaDAO.findById(id)
    }
}