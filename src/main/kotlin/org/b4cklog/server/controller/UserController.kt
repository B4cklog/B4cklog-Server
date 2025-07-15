package org.b4cklog.server.controller

import org.b4cklog.server.dto.IGDBGame
import org.b4cklog.server.dto.FriendRequestDto
import org.b4cklog.server.dto.FriendDto
import org.b4cklog.server.model.game.GameListType
import org.b4cklog.server.model.user.FriendRequestStatus
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

    @GetMapping("/{id}/profile/withGames")
    suspend fun getUserProfileWithGamesById(
        @PathVariable id: Int
    ): Map<String, Any> {
        val user = userDAO.getUser(id).orElseThrow { RuntimeException("User not found") }
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

    @PostMapping("/friends/request")
    fun sendFriendRequest(
        @RequestHeader("Authorization") authHeader: String,
        @RequestParam receiverId: Int
    ) {
        val token = authHeader.removePrefix("Bearer ").trim()
        val sender = authService.getUserByToken(token)
        userDAO.sendFriendRequest(sender.id, receiverId)
    }

    @PostMapping("/friends/request/{requestId}/accept")
    fun acceptFriendRequest(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable requestId: Int
    ) {
        val token = authHeader.removePrefix("Bearer ").trim()
        val user = authService.getUserByToken(token)
        userDAO.acceptFriendRequest(requestId, user.id)
    }

    @PostMapping("/friends/request/{requestId}/reject")
    fun rejectFriendRequest(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable requestId: Int
    ) {
        val token = authHeader.removePrefix("Bearer ").trim()
        val user = authService.getUserByToken(token)
        userDAO.rejectFriendRequest(requestId, user.id)
    }

    @DeleteMapping("/friends/request/{requestId}")
    fun cancelFriendRequest(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable requestId: Int
    ) {
        val token = authHeader.removePrefix("Bearer ").trim()
        val user = authService.getUserByToken(token)
        userDAO.cancelFriendRequest(requestId, user.id)
    }

    @GetMapping("/friends/requests/incoming")
    fun getIncomingFriendRequests(
        @RequestHeader("Authorization") authHeader: String
    ): List<FriendRequestDto> {
        val token = authHeader.removePrefix("Bearer ").trim()
        val user = authService.getUserByToken(token)
        return userDAO.getPendingFriendRequests(user.id).map {
            FriendRequestDto(
                id = it.id,
                senderId = it.sender.id,
                senderUsername = it.sender.username,
                receiverId = it.receiver.id,
                receiverUsername = it.receiver.username,
                status = it.status
            )
        }
    }

    @GetMapping("/friends/requests/outgoing")
    fun getOutgoingFriendRequests(
        @RequestHeader("Authorization") authHeader: String
    ): List<FriendRequestDto> {
        val token = authHeader.removePrefix("Bearer ").trim()
        val user = authService.getUserByToken(token)
        return userDAO.getSentFriendRequests(user.id).map {
            FriendRequestDto(
                id = it.id,
                senderId = it.sender.id,
                senderUsername = it.sender.username,
                receiverId = it.receiver.id,
                receiverUsername = it.receiver.username,
                status = it.status
            )
        }
    }

    @GetMapping("/friends")
    fun getFriends(
        @RequestHeader("Authorization") authHeader: String,
        @RequestParam(required = false) userId: Int? = null
    ): List<FriendDto> {
        val token = authHeader.removePrefix("Bearer ").trim()
        val user = if (userId == null) authService.getUserByToken(token) else userDAO.getUser(userId).orElseThrow { RuntimeException("User not found") }
        return userDAO.getFriends(user.id).map {
            FriendDto(
                id = it.id,
                username = it.username,
                firstName = it.firstName,
                lastName = it.lastName
            )
        }
    }

    @GetMapping("/{id}/friends")
    fun getFriendsById(
        @PathVariable id: Int
    ): List<FriendDto> {
        val user = userDAO.getUser(id).orElseThrow { RuntimeException("User not found") }
        return userDAO.getFriends(user.id).map {
            FriendDto(
                id = it.id,
                username = it.username,
                firstName = it.firstName,
                lastName = it.lastName
            )
        }
    }

    @DeleteMapping("/friends/{friendId}")
    fun removeFriend(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable friendId: Int
    ) {
        val token = authHeader.removePrefix("Bearer ").trim()
        val user = authService.getUserByToken(token)
        userDAO.removeFriend(user.id, friendId)
    }
}