package com.rarmash.b4cklog_server.dto

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String = "",
    val lastName: String = "",
    val age: Int = 0
)