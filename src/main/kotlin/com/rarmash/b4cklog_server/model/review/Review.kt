package com.rarmash.b4cklog_server.model.review

import com.rarmash.b4cklog_server.model.game.Game
import com.rarmash.b4cklog_server.model.user.User
import jakarta.persistence.*

@Entity
data class Review(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne
    val game: Game,
    @ManyToOne
    val user: User,
    val rating: Int,  // от 1 до 5
    @Column(columnDefinition = "TEXT")
        val comment: String? = null
)