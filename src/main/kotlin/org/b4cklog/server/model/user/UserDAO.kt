package org.b4cklog.server.model.user

import org.b4cklog.server.model.game.Game
import org.b4cklog.server.model.game.GameListType
import org.b4cklog.server.model.game.GameRepository
import org.b4cklog.server.util.HashUtils
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import org.b4cklog.server.model.user.FriendRequest
import org.b4cklog.server.model.user.FriendRequestRepository
import org.b4cklog.server.model.user.FriendRequestStatus

@Service
class UserDAO(
    private val repository: UserRepository,
    private val gameRepository: GameRepository,
    private val friendRequestRepository: FriendRequestRepository
) {
    fun addUser(user: User) = repository.save(user)

    fun getUser(id: Int) = repository.findById(id)

    fun getUserByUsername(username: String) = repository.findByUsername(username)

    fun getAllUsers() = repository.findAll().toList()

    fun deleteUser(id: Int) = repository.deleteById(id)

    fun saveUser(user: User) = repository.save(user)

    @Transactional
    fun addGameToList(userId: Int, gameId: Int, listName: String) {
        val user = getUser(userId).orElseThrow { RuntimeException("User not found") }

        // First, remove the game from all lists
        removeGameFromAllLists(userId, gameId)

        // Add the game to the required list
        val listType = when (listName) {
            "wantToPlay" -> GameListType.WANT_TO_PLAY
            "playing" -> GameListType.PLAYING
            "played" -> GameListType.PLAYED
            "completed" -> GameListType.COMPLETED
            "completed100" -> GameListType.COMPLETED_100
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid list name")
        }

        val game = Game(user = user, gameId = gameId, listType = listType)
        gameRepository.save(game)
    }

    @Transactional
    fun removeGameFromAllLists(userId: Int, gameId: Int) {
        gameRepository.deleteByUserIdAndGameId(userId, gameId)
    }

    fun getGamesByListType(userId: Int, listType: GameListType): List<Int> {
        return gameRepository.findByUserIdAndListType(userId, listType)
            .map { it.gameId }
    }

    fun getAllUserGames(userId: Int): List<Game> {
        return GameListType.values().flatMap { listType ->
            gameRepository.findByUserIdAndListType(userId, listType)
        }
    }

    fun getAllUserGameIds(userId: Int): Map<GameListType, List<Int>> {
        return GameListType.values().associateWith { listType ->
            getGamesByListType(userId, listType)
        }
    }

    @Transactional
    fun updateEmail(userId: Int, newEmail: String) {
        val user = getUser(userId).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User not found") }

        if (repository.findByEmail(newEmail) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "This email is already in use")
        }

        user.email = newEmail
        saveUser(user)
    }

    @Transactional
    fun updatePassword(userId: Int, newPassword: String) {
        val user = getUser(userId).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User not found") }

        user.password = HashUtils.sha256(newPassword)
        saveUser(user)
    }

    fun sendFriendRequest(senderId: Int, receiverId: Int) {
        if (senderId == receiverId) throw IllegalArgumentException("Cannot add yourself as a friend")
        val sender = getUser(senderId).orElseThrow { RuntimeException("Sender not found") }
        val receiver = getUser(receiverId).orElseThrow { RuntimeException("Receiver not found") }
        val existing = friendRequestRepository.findBetweenUsers(senderId, receiverId)
        if (existing.any { it.status == FriendRequestStatus.PENDING }) {
            throw RuntimeException("Request already sent")
        }
        if (existing.any { it.status == FriendRequestStatus.ACCEPTED }) {
            throw RuntimeException("You are already friends")
        }
        friendRequestRepository.save(FriendRequest(sender = sender, receiver = receiver))
    }

    fun acceptFriendRequest(requestId: Int, userId: Int) {
        val request = friendRequestRepository.findById(requestId).orElseThrow { RuntimeException("Request not found") }
        if (request.receiver.id != userId) throw RuntimeException("No rights to accept")
        request.status = FriendRequestStatus.ACCEPTED
        friendRequestRepository.save(request)
    }

    fun rejectFriendRequest(requestId: Int, userId: Int) {
        val request = friendRequestRepository.findById(requestId).orElseThrow { RuntimeException("Request not found") }
        if (request.receiver.id != userId) throw RuntimeException("No rights to reject")
        request.status = FriendRequestStatus.REJECTED
        friendRequestRepository.save(request)
    }

    fun cancelFriendRequest(requestId: Int, userId: Int) {
        val request = friendRequestRepository.findById(requestId).orElseThrow { RuntimeException("Request not found") }
        if (request.sender.id != userId) throw RuntimeException("No rights to cancel the request")
        if (request.status != FriendRequestStatus.PENDING) throw RuntimeException("Only pending requests can be cancelled")
        friendRequestRepository.deleteById(requestId)
    }

    fun removeFriend(userId: Int, friendId: Int) {
        val requests = friendRequestRepository.findBetweenUsers(userId, friendId).filter { it.status == FriendRequestStatus.ACCEPTED }
        requests.forEach { friendRequestRepository.deleteById(it.id) }
    }

    fun getFriends(userId: Int): List<User> {
        val accepted = friendRequestRepository.findAllByUserAndStatus(userId, FriendRequestStatus.ACCEPTED)
        return accepted
            .filter { it.status == FriendRequestStatus.ACCEPTED }
            .map {
                if (it.sender.id == userId) it.receiver else it.sender
            }
            .distinctBy { it.id }
    }

    fun getPendingFriendRequests(userId: Int): List<FriendRequest> {
        return friendRequestRepository.findByReceiverIdAndStatus(userId, FriendRequestStatus.PENDING)
    }

    fun getSentFriendRequests(userId: Int): List<FriendRequest> {
        return friendRequestRepository.findBySenderIdAndStatus(userId, FriendRequestStatus.PENDING)
    }
}