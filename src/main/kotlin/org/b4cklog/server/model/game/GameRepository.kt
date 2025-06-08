package org.b4cklog.server.model.game

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : CrudRepository<Game, Int> {
    @Query("SELECT g FROM Game g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :namePart, '%'))")
    fun findByNameContainingIgnoreCase(@Param("namePart") namePart: String): List<Game>
}