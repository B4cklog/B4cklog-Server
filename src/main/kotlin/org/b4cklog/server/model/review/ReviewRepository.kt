package org.b4cklog.server.model.review

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository : CrudRepository<Review, Int> {

    fun findAllByGameId(gameId: Int): List<Review>

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.game.id = :gameId")
    fun findAverageRatingByGameId(gameId: Int): Double?

    fun findByUserIdAndGameId(userId: Int, gameId: Int): Review?
}