package services

import model.ClubDAO
import org.jetbrains.exposed.sql.transactions.transaction

class ClubService {
    fun getAllClubs(): List<ClubDAO> = transaction{
        ClubDAO.all().toList()
    }

    fun getClubById(id: Int): ClubDAO? = transaction {
        ClubDAO.findById(id)
    }

    fun createClub(nombreClub: String, direccion: String?, telefono: String?): ClubDAO = transaction{
        ClubDAO.new {
            this.nombreClub = nombreClub
            this.direccion = direccion
            this.telefono = telefono
        }
    }

    fun updateClub(id: Int, nombreClub: String?, direccion: String?, telefono: String?): Boolean = transaction {
        val clubDAO = ClubDAO.findById(id) ?: return@transaction false

        nombreClub?.let { clubDAO.nombreClub = it }
        direccion?.let { clubDAO.direccion = it }
        telefono?.let { clubDAO.telefono = it }
        true
    }

    fun deleteClub(id: Int): Boolean = transaction {
        val clubDAO = ClubDAO.findById(id) ?: return@transaction false
        clubDAO.delete()
        true
    }
}