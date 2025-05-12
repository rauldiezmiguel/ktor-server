package application

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import model.EquipoDAO
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction

object DatabaseFactory {
    private val config = HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/futbol_base_tfg"
        username = System.getenv("DB_USERNAME") ?: "raul_admin"
        password = System.getenv("DB_PASSWORD") ?: "Rtc10DMlb!"
        maximumPoolSize = 10
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }

    fun init() {
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)
    }

    //fun <T> transaction(statement: (Transaction) -> List<EquipoDAO>): T = org.jetbrains.exposed.sql.transactions.transaction { statement() }
}