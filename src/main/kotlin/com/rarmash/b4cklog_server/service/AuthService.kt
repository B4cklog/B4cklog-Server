package com.rarmash.b4cklog_server.service

import com.rarmash.b4cklog_server.dto.LoginRequest
import com.rarmash.b4cklog_server.dto.LoginResponse
import com.rarmash.b4cklog_server.dto.RegisterRequest
import com.rarmash.b4cklog_server.dto.RegisterResponse
import com.rarmash.b4cklog_server.model.token.*
import com.rarmash.b4cklog_server.model.user.User
import com.rarmash.b4cklog_server.model.user.UserRepository
import com.rarmash.b4cklog_server.util.HashUtils
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) {

    fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByUsername(request.username)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found")

        val hashedInput = HashUtils.sha256(request.password)
        if (user.password != hashedInput) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password")
        }

        val tokenString = UUID.randomUUID().toString()
        val token = Token(user = user, token = tokenString)
        tokenRepository.save(token)

        return LoginResponse(tokenString)
    }

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

        val tokenString = UUID.randomUUID().toString()
        val token = Token(user = savedUser, token = tokenString)
        tokenRepository.save(token)

        return RegisterResponse(tokenString)
    }

    fun checkAdmin(tokenString: String): User {
        val user = getUserByToken(tokenString)
        if (!user.isAdmin) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Требуются права администратора")
        }
        return user
    }

    fun getUserByToken(tokenString: String): User {
        val token = tokenRepository.findByToken(tokenString)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Недействительный токен")
        return token.user
    }
}
