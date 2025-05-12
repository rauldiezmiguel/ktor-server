package authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.RegisteredClaims.AUDIENCE
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import sun.security.util.KeyUtil.validate
import java.util.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import services.UserService

object AuthConfig {
    private val SECRET: String = System.getenv("JWT_SECRET") ?: throw IllegalStateException("JWT_SECRET is not set in the environment")
    private const val ISSUER = "ktor-server"
    private const val EXPIRATION_TIME = 36_000_00 * 10 // 10 horas de validez para el token
    private const val REALM = "ktor-realm"
    private const val EXPIRATION_TIME_REFRESH = 604_800_000 // 7 días de validez

    private val algorithm = Algorithm.HMAC256(SECRET) // Algoritmo HMAC256 para firmar JWT

    // Verificador de tokens
    //val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(ISSUER).build()

    // Genra un token JWT para un usuario
    fun generateToken(usuario: String): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withClaim("usuario", usuario) // Incluye el nombre de usuario en el token
            .withExpiresAt(Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Expiración en 10 horas
            .sign(algorithm) // Firma el token con la clave secreta
    }

    fun generateRefreshToken(usuario: String): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withClaim("usuario", usuario) // Incluye el nombre de usuario en el token
            .withExpiresAt(Date(System.currentTimeMillis() + EXPIRATION_TIME_REFRESH)) // Expiración en 10 horas
            .sign(algorithm) // Firma el token con la clave secreta
    }

    fun verifyRefreshToken(refreshToken: String): String? {
        try {
            val algorithm = Algorithm.HMAC256(SECRET)
            val decodedJWT = JWT.require(algorithm)
                .build()
                .verify(refreshToken)

            // Obtener el nombre de usuario desde el token
            return decodedJWT.getClaim("usuario").asString()
        } catch (e: JWTVerificationException) {
            return e.message
        }
    }

    fun Application.configureAuthentication() {
        install(Authentication) {
            jwt("auth-jwt"){
                realm = REALM
                verifier(
                    JWT.require(algorithm)
                        .withIssuer(ISSUER)
                        .build()
                )
                validate { credential ->
                    if (credential.payload.getClaim("usuario").asString().isNotEmpty()) {
                        JWTPrincipal(credential.payload)
                    }else {
                        null
                    }
                }
            }
        }

        routing {
            authenticate("auth-jwt") {
                get("/protected") {
                    call.respondText("Ruta protegida: acceso autorizado")
                }
            }
        }
    }
}