package org.b4cklog.server.dto

data class UserProfileResponse(
    val user: UserDto,
    val games: Map<String, List<IGDBGame>>
)
