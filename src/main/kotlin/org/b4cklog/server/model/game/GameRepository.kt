package org.b4cklog.server.model.game

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface GameRepository : CrudRepository<Game, Int> {
    fun findByUserIdAndListType(userId: Int, listType: GameListType): List<Game>
    fun findByUserIdAndGameId(userId: Int, gameId: Int): List<Game>
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Game g WHERE g.user.id = :userId AND g.gameId = :gameId")
    fun deleteByUserIdAndGameId(userId: Int, gameId: Int)
} 