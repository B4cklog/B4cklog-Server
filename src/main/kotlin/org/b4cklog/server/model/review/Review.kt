package org.b4cklog.server.model.review

import org.b4cklog.server.model.user.User
import jakarta.persistence.*

@Entity
data class Review(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    val gameId: Int,
    @ManyToOne
    val user: User,
    val rating: Int,  // от 1 до 5
    @Column(columnDefinition = "TEXT")
    val comment: String? = null
)