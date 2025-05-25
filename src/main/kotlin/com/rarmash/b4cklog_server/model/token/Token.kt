package com.rarmash.b4cklog_server.model.token

import com.rarmash.b4cklog_server.model.user.User
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