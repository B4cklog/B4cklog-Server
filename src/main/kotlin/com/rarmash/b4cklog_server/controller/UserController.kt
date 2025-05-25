package com.rarmash.b4cklog_server.controller

import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus
import com.rarmash.b4cklog_server.model.game.GameDAO
import com.rarmash.b4cklog_server.model.user.UserDAO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userDAO: UserDAO,
    private val gameDAO: GameDAO
) {
    @PostMapping("/{userId}/addGameToList")
    fun addGameToList(
        @PathVariable userId: Int,
        @RequestParam gameId: Int,
        @RequestParam listName: String
    ) {
        val game = gameDAO.getGame(gameId).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Игра не найдена") }
        userDAO.addGameToList(userId, game, listName)
    }

    @DeleteMapping("/{userId}/removeGameFromAllLists")
    fun removeGameFromAllLists(
        @PathVariable userId: Int,
        @RequestParam gameId: Int
    ) {
        val game = gameDAO.getGame(gameId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Игра не найдена") }
        userDAO.removeGameFromAllLists(userId, game)
    }
}