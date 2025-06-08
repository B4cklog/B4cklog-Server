package org.b4cklog.server.model.user

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, Int> {
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?
}