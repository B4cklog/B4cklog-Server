package org.b4cklog.server.model.user

import org.b4cklog.server.model.game.Game
import jakarta.persistence.*

@Entity
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(unique = true)
    var username: String,
    var firstName: String = "",
    var lastName: String = "",
    @Column(unique = true)
    var email: String = "",
    var password: String = "",
    var age: Int = 0,
    var isAdmin: Boolean = false,

    // B4cklog lists
    @ManyToMany
    var backlogWantToPlay: MutableList<Game> = mutableListOf(),
    @ManyToMany
    var backlogPlaying: MutableList<Game> = mutableListOf(),
    @ManyToMany
    var backlogPlayed: MutableList<Game> = mutableListOf(),
    @ManyToMany
    var backlogCompleted: MutableList<Game> = mutableListOf(),
    @ManyToMany
    var backlogCompleted100: MutableList<Game> = mutableListOf()
) {
    constructor(id: Int) : this(id = id, username = "", password = "", isAdmin = false)
}