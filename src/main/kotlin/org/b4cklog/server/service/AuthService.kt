package org.b4cklog.server.service

import org.b4cklog.server.dto.LoginRequest
import org.b4cklog.server.dto.LoginResponse
import org.b4cklog.server.dto.RegisterRequest
import org.b4cklog.server.dto.RegisterResponse
import org.b4cklog.server.model.token.*
import org.b4cklog.server.model.user.User
import org.b4cklog.server.model.user.UserRepository
import org.b4cklog.server.util.HashUtils
import org.b4cklog.server.util.JwtUtils
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    @Transactional
    fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByUsername(request.username)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found")

        val hashedInput = HashUtils.sha256(request.password)
        if (user.password != hashedInput) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password")
        }

        val accessToken = JwtUtils.generateAccessToken(user.id, user.username)
        val refreshToken = JwtUtils.generateRefreshToken(user.id)
        val expiry = JwtUtils.getRefreshTokenExpiry(refreshToken)
        val sessionId = UUID.randomUUID().toString()
        refreshTokenRepository.save(RefreshToken(token = refreshToken, user = user, expiryDate = expiry, sessionId = sessionId))

        return LoginResponse(accessToken, refreshToken, sessionId)
    }

    @Transactional
    fun register(request: RegisterRequest): RegisterResponse {
        if (userRepository.findByUsername(request.username) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Username already taken")
        }

        if (userRepository.findByEmail(request.email) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email already registered")
        }

        val user = User(
            username = request.username,
            email = request.email,
            password = HashUtils.sha256(request.password),
            firstName = request.firstName,
            lastName = request.lastName,
            age = request.age
        )

        val savedUser = userRepository.save(user)

        val accessToken = JwtUtils.generateAccessToken(savedUser.id, savedUser.username)
        val refreshToken = JwtUtils.generateRefreshToken(savedUser.id)
        val expiry = JwtUtils.getRefreshTokenExpiry(refreshToken)
        val sessionId = UUID.randomUUID().toString()
        refreshTokenRepository.save(RefreshToken(token = refreshToken, user = savedUser, expiryDate = expiry, sessionId = sessionId))

        return RegisterResponse(accessToken, refreshToken, sessionId)
    }

    fun refreshToken(refreshToken: String, sessionId: String): LoginResponse {
        val userId = JwtUtils.getUserIdFromToken(refreshToken)?.toInt()
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token")
        val tokenEntity = refreshTokenRepository.findByTokenAndSessionId(refreshToken, sessionId)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token")
        if (!JwtUtils.validateToken(refreshToken) || tokenEntity.user.id != userId) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token")
        }
        val user = tokenEntity.user
        val newAccessToken = JwtUtils.generateAccessToken(user.id, user.username)
        return LoginResponse(newAccessToken, refreshToken, sessionId)
    }

    fun checkAdmin(tokenString: String): User {
        val user = getUserByToken(tokenString)
        if (!user.isAdmin) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Требуются права администратора")
        }
        return user
    }

    fun getUserByToken(tokenString: String): User {
        // access_token теперь JWT
        val userId = JwtUtils.getUserIdFromToken(tokenString)?.toInt()
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Недействительный токен")
        return userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не найден")
        }
    }

    fun logout(refreshToken: String, sessionId: String) {
        refreshTokenRepository.deleteByTokenAndSessionId(refreshToken, sessionId)
    }
}
