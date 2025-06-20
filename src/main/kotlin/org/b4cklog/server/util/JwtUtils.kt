package org.b4cklog.server.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.util.*

object JwtUtils {
    private const val ACCESS_TOKEN_EXPIRATION_MINUTES = 15L
    private const val REFRESH_TOKEN_EXPIRATION_DAYS = 7L
    private val jwtSecret = System.getenv("JWT_SECRET")?.toByteArray() ?: "0123456789abcdef0123456789abcdef".toByteArray()
    private val key = Keys.hmacShaKeyFor(jwtSecret)

    fun generateAccessToken(userId: Int, username: String): String {
        val now = Date()
        val expiry = Date(now.time + ACCESS_TOKEN_EXPIRATION_MINUTES * 60 * 1000)
        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("username", username)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun generateRefreshToken(userId: Int): String {
        val now = Date()
        val expiry = Date(now.time + REFRESH_TOKEN_EXPIRATION_DAYS * 24 * 60 * 60 * 1000)
        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUserIdFromToken(token: String): Long? {
        return try {
            val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
            claims.subject.toLongOrNull()
        } catch (e: Exception) {
            null
        }
    }

    fun getUsernameFromToken(token: String): String? {
        return try {
            val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
            claims["username"] as? String
        } catch (e: Exception) {
            null
        }
    }

    fun getRefreshTokenExpiry(token: String): Date {
        return try {
            val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
            claims.expiration
        } catch (e: Exception) {
            Date()
        }
    }
} 