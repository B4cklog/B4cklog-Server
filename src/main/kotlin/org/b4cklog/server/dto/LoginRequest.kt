package org.b4cklog.server.dto

data class LoginRequest(
    val username: String,
    val password: String
)