package com.rarmash.b4cklog_server.controller

import com.rarmash.b4cklog_server.model.game.Game
import com.rarmash.b4cklog_server.model.game.GameDAO
import com.rarmash.b4cklog_server.model.user.User
import com.rarmash.b4cklog_server.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/games")
class GameController (
    private val gameDAO: GameDAO,
    private val authService: AuthService
) {
    private fun checkAdmin(tokenString: String): User {
        val user = authService.getUserByToken(tokenString)
        if (!user.isAdmin) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Требуются права администратора")
        }
        return user
    }

    @GetMapping("/add")
    fun addGame(
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody game: Game
    ): Game {
        val token = authHeader.removePrefix("Bearer ").trim()
        checkAdmin(token)
        return gameDAO.addGame(game)
    }

    @GetMapping("/get/{id}")
    fun getGame(@PathVariable("id") id: Int): Optional<Game> {
        return gameDAO.getGame(id = id)
    }

    @GetMapping("/get/all")
    fun getAllGames(): List<Game> {
        return gameDAO.getAllGames()
    }

    @GetMapping("/delete/{id}")
    fun deleteGame(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable("id") id: Int
    ) {
        val token = authHeader.removePrefix("Bearer ").trim()
        checkAdmin(token)
        return gameDAO.deleteGame(id = id)
    }

    @GetMapping("/search")
    fun searchGames(@RequestParam("q") query: String): List<Game> {
        return gameDAO.searchGamesByName(query)
    }
}