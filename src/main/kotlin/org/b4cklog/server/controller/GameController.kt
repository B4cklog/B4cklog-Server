package org.b4cklog.server.controller

import org.b4cklog.server.model.game.Game
import org.b4cklog.server.model.game.GameDAO
import org.b4cklog.server.service.AuthService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/games")
class GameController (
    private val gameDAO: GameDAO,
    private val authService: AuthService
) {
    @PostMapping("/add")
    fun addGame(
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody game: Game
    ): Game {
        val token = authHeader.removePrefix("Bearer ").trim()
        authService.checkAdmin(token)
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

    @DeleteMapping("/delete/{id}")
    fun deleteGame(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable("id") id: Int
    ) {
        val token = authHeader.removePrefix("Bearer ").trim()
        authService.checkAdmin(token)
        return gameDAO.deleteGame(id = id)
    }

    @GetMapping("/search")
    fun searchGames(@RequestParam("q") query: String): List<Game> {
        return gameDAO.searchGamesByName(query)
    }

    @PostMapping("/update")
    fun updateGame(
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody game: Game
    ): Game {
        val token = authHeader.removePrefix("Bearer ").trim()
        authService.checkAdmin(token)
        return gameDAO.updateGame(game)
    }
}