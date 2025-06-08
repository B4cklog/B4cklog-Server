package org.b4cklog.server.dto

data class ReviewResponse(
    val rating: Int,
    val comment: String?
)