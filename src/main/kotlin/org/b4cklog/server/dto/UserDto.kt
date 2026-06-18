package org.b4cklog.server.dto

import org.b4cklog.server.model.user.User

data class UserDto(
    val id: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val age: Int,
    val isAdmin: Boolean
)

fun User.toUserDto(includePrivateFields: Boolean = false) = UserDto(
    id = id,
    username = username,
    firstName = firstName,
    lastName = lastName,
    email = email.takeIf { includePrivateFields },
    age = age,
    isAdmin = isAdmin
)
