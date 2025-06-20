package org.b4cklog.server.model.game

import org.b4cklog.server.model.user.User
import jakarta.persistence.*

@Entity
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,
    
    val gameId: Int,
    
    @Enumerated(EnumType.STRING)
    val listType: GameListType
)

enum class GameListType {
    WANT_TO_PLAY,
    PLAYING,
    PLAYED,
    COMPLETED,
    COMPLETED_100
} 