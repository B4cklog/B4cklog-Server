package org.b4cklog.server.dto

data class RegisterResponse(
    val accessToken: String,
    val refreshToken: String
)