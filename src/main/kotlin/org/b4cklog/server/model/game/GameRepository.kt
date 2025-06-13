package org.b4cklog.server.model.game

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : CrudRepository<Game, Int> {
    @Query("SELECT g FROM Game g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :namePart, '%'))")
    fun findByNameContainingIgnoreCase(@Param("namePart") namePart: String): List<Game>

    @Query("SELECT g FROM Game g ORDER BY g.releaseDate DESC LIMIT 10")
    fun findLatestGames(): List<Game>

    // TODO: change to popularity
    @Query("SELECT g FROM Game g ORDER BY g.id DESC LIMIT 10")
    fun findPopularGames(): List<Game>
}