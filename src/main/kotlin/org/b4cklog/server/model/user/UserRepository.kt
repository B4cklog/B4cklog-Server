package org.b4cklog.server.model.user

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, Int> {
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?

    @Query("""
        SELECT u FROM User u
        WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
          AND u.id <> :currentUserId
    """)
    fun searchUsers(
        @Param("query") query: String,
        @Param("currentUserId") currentUserId: Int
    ): List<User>
}