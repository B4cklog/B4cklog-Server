package org.b4cklog.server.controller

import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus
import org.b4cklog.server.model.game.GameDAO
import org.b4cklog.server.model.user.User
import org.b4cklog.server.model.user.UserDAO
import org.b4cklog.server.service.AuthService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userDAO: UserDAO,
    private val gameDAO: GameDAO,
    private val authService: AuthService
) {
    @GetMapping("/profile")
    fun getUserProfile(
        @RequestHeader("Authorization") authHeader: String
    ): User {
        val token = authHeader.removePrefix("Bearer ").trim()
        val user = authService.getUserByToken(token)
        return user
    }

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

    @PatchMapping("/updateEmail")
    fun updateEmail(
        @RequestHeader("Authorization") authHeader: String,
        @RequestParam newEmail: String
    ) {
        val token = authHeader.removePrefix("Bearer ").trim()
        val user = authService.getUserByToken(token)

        userDAO.updateEmail(user.id, newEmail)
    }

    @PatchMapping("/updatePassword")
    fun updatePassword(
        @RequestHeader("Authorization") authHeader: String,
        @RequestParam newPassword: String
    ) {
        val token = authHeader.removePrefix("Bearer ").trim()
        val user = authService.getUserByToken(token)

        userDAO.updatePassword(user.id, newPassword)
    }
}