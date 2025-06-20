package org.b4cklog.server.controller

import org.b4cklog.server.dto.IGDBGame
import org.b4cklog.server.model.game.GameListType
import org.b4cklog.server.model.user.User
import org.b4cklog.server.model.user.UserDAO
import org.b4cklog.server.service.AuthService
import org.b4cklog.server.service.IGDBService
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/users")
class UserController(
    private val userDAO: UserDAO,
    private val igdbService: IGDBService,
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

    @GetMapping("/profile/withGames")
    suspend fun getUserProfileWithGames(
        @RequestHeader("Authorization") authHeader: String
    ): Map<String, Any> {
        val token = authHeader.removePrefix("Bearer ").trim()
        val user = authService.getUserByToken(token)
        
        val userGames = userDAO.getAllUserGames(user.id)
        
        val games = mutableMapOf<String, List<IGDBGame>>()
        for (listType in GameListType.values()) {
            val gameIds = userGames
                .filter { it.listType == listType }
                .map { it.gameId }
            
            val gameList = gameIds.mapNotNull { id ->
                igdbService.getGameById(id)
            }
            games[listType.name.lowercase()] = gameList
        }
        
        return mapOf(
            "user" to user,
            "games" to games
        )
    }

    @PostMapping("/{userId}/addGameToList")
    @Transactional
    fun addGameToList(
        @PathVariable userId: Int,
        @RequestParam gameId: Int,
        @RequestParam listName: String
    ) {
        userDAO.addGameToList(userId, gameId, listName)
    }

    @DeleteMapping("/{userId}/removeGameFromAllLists")
    @Transactional
    fun removeGameFromAllLists(
        @PathVariable userId: Int,
        @RequestParam gameId: Int
    ) {
        userDAO.removeGameFromAllLists(userId, gameId)
    }

    @PatchMapping("/updateEmail")
    @Transactional
    fun updateEmail(
        @RequestHeader("Authorization") authHeader: String,
        @RequestParam newEmail: String
    ) {
        val token = authHeader.removePrefix("Bearer ").trim()
        val user = authService.getUserByToken(token)

        userDAO.updateEmail(user.id, newEmail)
    }

    @PatchMapping("/updatePassword")
    @Transactional
    fun updatePassword(
        @RequestHeader("Authorization") authHeader: String,
        @RequestParam newPassword: String
    ) {
        val token = authHeader.removePrefix("Bearer ").trim()
        val user = authService.getUserByToken(token)

        userDAO.updatePassword(user.id, newPassword)
    }
}