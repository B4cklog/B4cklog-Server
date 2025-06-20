package org.b4cklog.server.model.token

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByTokenAndSessionId(token: String, sessionId: String): RefreshToken?
    fun deleteByTokenAndSessionId(token: String, sessionId: String)
} 