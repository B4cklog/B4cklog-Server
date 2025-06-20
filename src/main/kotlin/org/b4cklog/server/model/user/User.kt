package org.b4cklog.server.model.user

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
    var isAdmin: Boolean = false
) {
    constructor(id: Int) : this(id = id, username = "", password = "", isAdmin = false)
}