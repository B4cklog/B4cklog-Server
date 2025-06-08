package org.b4cklog.server.dto

data class ReviewRequest(
    val userId: Int,
    val gameId: Int,
    val rating: Int,
    val comment: String? = null
)