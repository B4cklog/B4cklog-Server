package org.b4cklog.server.model.token

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TokenRepository : CrudRepository<Token, Int> {
    fun findByToken(token: String): Token?
    fun deleteByToken(token: String)
}