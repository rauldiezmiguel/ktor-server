package authentication

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordHasher {

    /**
     * Cifra una contraseña usando BCrypt.
     * @param password Contraseña en texto plano.
     * @return Contraseña cifrada.
     */
    fun hashPassword(passWrd: String): String {
        return BCrypt.withDefaults().hashToString(12, passWrd.toCharArray()) // 12 es el coste del hashing
    }

    /**
     * Verifica si una contraseña en texto plano coincide con el hash almacenado.
     * @param plainPassword Contraseña ingresada por el usuario.
     * @param hashedPassword Contraseña cifrada almacenada en la base de datos.
     * @return `true` si coinciden, `false` si no.
     */
    fun verifyPassword(passWrd: String, hashedPassword: String): Boolean {
        return BCrypt.verifyer().verify(passWrd.toCharArray(), hashedPassword).verified
    }
}