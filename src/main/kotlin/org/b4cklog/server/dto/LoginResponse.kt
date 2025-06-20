package org.b4cklog.server.dto

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)