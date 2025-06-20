package org.b4cklog.server.model.review

import org.b4cklog.server.model.user.User
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
            ?: Review(user = User(id = userId), gameId = gameId, rating = rating, comment = comment)

        repository.save(review)
    }
}