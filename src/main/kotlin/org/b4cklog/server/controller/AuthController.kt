package org.b4cklog.server.controller

import org.b4cklog.server.dto.LoginRequest
import org.b4cklog.server.dto.LoginResponse
import org.b4cklog.server.dto.RegisterRequest
import org.b4cklog.server.dto.RegisterResponse
import org.b4cklog.server.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<RegisterResponse> {
        val response = authService.register(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody body: Map<String, String>): ResponseEntity<LoginResponse> {
        val refreshToken = body["refreshToken"] ?: return ResponseEntity.badRequest().build()
        val sessionId = body["sessionId"] ?: return ResponseEntity.badRequest().build()
        val response = authService.refreshToken(refreshToken, sessionId)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    fun logout(@RequestBody body: Map<String, String>): ResponseEntity<Void> {
        val refreshToken = body["refreshToken"] ?: return ResponseEntity.badRequest().build()
        val sessionId = body["sessionId"] ?: return ResponseEntity.badRequest().build()
        authService.logout(refreshToken, sessionId)
        return ResponseEntity.ok().build()
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<Map<String, String>> {
        val body = mapOf("error" to (ex.reason ?: "Error"))
        return ResponseEntity.status(ex.statusCode).body(body)
    }
}