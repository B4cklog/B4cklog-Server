package com.rarmash.b4cklog_server.controller

import com.rarmash.b4cklog_server.dto.ReviewRequest
import com.rarmash.b4cklog_server.dto.ReviewResponse
import com.rarmash.b4cklog_server.model.review.ReviewDAO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reviews")
class ReviewController(private val reviewDAO: ReviewDAO) {

    @GetMapping("/user/{userId}/game/{gameId}")
    fun getUserReview(
        @PathVariable userId: Int,
        @PathVariable gameId: Int
    ): ResponseEntity<ReviewResponse> {
        val review = reviewDAO.getUserReviewForGame(userId, gameId)
            ?: return ResponseEntity.notFound().build()

        val response = ReviewResponse(
            rating = review.rating,
            comment = review.comment
        )

        return ResponseEntity.ok(response)
    }

    @PostMapping("/add")
    fun addOrUpdateReview(@RequestBody request: ReviewRequest): ResponseEntity<String> {
        if (request.rating !in 1..10) {
            return ResponseEntity.badRequest().body("Рейтинг должен быть от 1 до 10")
        }

        reviewDAO.addOrUpdateReview(
            userId = request.userId,
            gameId = request.gameId,
            rating = request.rating,
            comment = request.comment
        )

        return ResponseEntity.status(HttpStatus.CREATED).body("Отзыв добавлен/обновлён")
    }

    @GetMapping("/game/{gameId}/average")
    fun getAverageRating(@PathVariable gameId: Int): ResponseEntity<Double> {
        val avg = reviewDAO.getAverageRating(gameId)
        return ResponseEntity.ok(avg)
    }
}