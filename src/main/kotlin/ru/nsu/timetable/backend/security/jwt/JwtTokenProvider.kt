package ru.nsu.timetable.backend.security.jwt

import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import ru.nsu.timetable.backend.entity.Role
import java.util.*
import jakarta.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider(
    private val userDetailsService: UserDetailsService,
    @Value("\${jwt.token.secret}")
    private var secret: String,
    @Value("\${jwt.token.expired}")
    private val validityInMilliseconds: Long
) {


    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    init {
        secret = Base64.getEncoder().encodeToString(secret.toByteArray())
    }


    fun createToken(username: String?, role: Role): Pair<String, Long> {
        val claims: Claims = Jwts.claims().setSubject(username)
        claims["roles"] = role.name
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)
        return Pair(
            Jwts.builder() //
                .setClaims(claims) //
                .setIssuedAt(now) //
                .setExpiration(validity) //
                .signWith(SignatureAlgorithm.HS256, secret) //
                .compact(),
            validityInMilliseconds / 1000
        )
    }

    fun getAuthentication(token: String?): Authentication? {
        val userDetails: UserDetails
        userDetails = try {
            userDetailsService!!.loadUserByUsername(
                getUsernameFromJwtToken(token)
            )
        } catch (e: Exception) {
            return null
        }
        println("Logged in as ${userDetails.username} with role ${userDetails.authorities}")
        return UsernamePasswordAuthenticationToken(
            userDetails,
            "",
            userDetails.authorities
        )
    }

    fun getUsernameFromJwtToken(token: String?): String {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject()
    }

    fun getBodyOfHeaderToken(token: String): String {
        return token.substring(7)
    }

    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

    fun validateToken(token: String?): Boolean {
        return try {
            val claims: Jws<Claims> = Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
            !claims.getBody().getExpiration().before(Date())
        } catch (e: JwtException) {
            throw JwtAuthenticationException("JWT token is expired or invalid")
        } catch (e: IllegalArgumentException) {
            throw JwtAuthenticationException("JWT token is expired or invalid")
        }
    }
}

