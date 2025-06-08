package org.b4cklog.server.model.token

import org.b4cklog.server.model.user.User
import jakarta.persistence.*

@Entity
data class Token (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne
    val user: User,
    @Column(unique = true)
    val token: String
)