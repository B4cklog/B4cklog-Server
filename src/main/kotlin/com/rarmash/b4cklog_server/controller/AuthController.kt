package com.rarmash.b4cklog_server.controller

import com.rarmash.b4cklog_server.dto.LoginRequest
import com.rarmash.b4cklog_server.dto.LoginResponse
import com.rarmash.b4cklog_server.dto.RegisterRequest
import com.rarmash.b4cklog_server.dto.RegisterResponse
import com.rarmash.b4cklog_server.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}