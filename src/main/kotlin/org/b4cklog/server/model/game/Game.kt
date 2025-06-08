package org.b4cklog.server.model.game

import org.b4cklog.server.model.platform.Platform
import jakarta.persistence.*

@Entity
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    var name: String,
    @Column(columnDefinition = "TEXT")
    var summary: String = "",
    var cover: String = "",
    var releaseDate: String = "",
    @ManyToMany
    var platforms: MutableList<Platform> = mutableListOf()
) {
    constructor(id: Int) : this(id = id, name = "", summary = "")
}