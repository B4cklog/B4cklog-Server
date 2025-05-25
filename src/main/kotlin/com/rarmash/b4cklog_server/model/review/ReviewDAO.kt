package com.rarmash.b4cklog_server.model.review

import com.rarmash.b4cklog_server.model.game.Game
import com.rarmash.b4cklog_server.model.user.User
import org.springframework.stereotype.Service

@Service
class ReviewDAO(private val repository: ReviewRepository) {

    fun addReview(review: Review) = repository.save(review)

    fun getReviewsForGame(gameId: Int): List<Review> = repository.findAllByGameId(gameId)

    fun getAverageRating(gameId: Int): Double {
        return repository.findAverageRatingByGameId(gameId) ?: 0.0
    }

    fun getUserReviewForGame(userId: Int, gameId: Int): Review? {
        return repository.findByUserIdAndGameId(userId, gameId)
    }

    fun addOrUpdateReview(userId: Int, gameId: Int, rating: Int, comment: String?) {
        val existing = repository.findByUserIdAndGameId(userId, gameId)

        val review = existing?.copy(rating = rating, comment = comment)
            ?: Review(user = User(id = userId), game = Game(id = gameId), rating = rating, comment = comment)

        repository.save(review)
    }
}