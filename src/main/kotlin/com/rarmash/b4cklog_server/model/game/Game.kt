package com.rarmash.b4cklog_server.model.game

import com.rarmash.b4cklog_server.model.platform.Platform
import jakarta.persistence.*

@Entity
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    var name: String,
    var summary: String = "",
    var cover: String = "",
    var releaseDate: String = "",
    @ManyToMany
    var platforms: MutableList<Platform> = mutableListOf()
) {
    constructor(id: Int) : this(id = id, name = "", summary = "")
}