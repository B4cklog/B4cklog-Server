package org.b4cklog.server.model.user

import org.b4cklog.server.model.game.Game
import org.b4cklog.server.model.game.GameListType
import org.b4cklog.server.model.game.GameRepository
import org.b4cklog.server.util.HashUtils
import org.springframework.http.HttpStatus
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

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

    @Transactional
    fun sendFriendRequest(senderId: Int, receiverId: Int) {
        if (senderId == receiverId) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add yourself as a friend")
        }
        val sender = getUser(senderId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found")
        }
        val receiver = getUser(receiverId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver not found")
        }
        val pairKey = FriendRequest.pairKey(senderId, receiverId)
        val existing = friendRequestRepository.findByPairKey(pairKey)

        when (existing?.status) {
            FriendRequestStatus.PENDING ->
                throw ResponseStatusException(HttpStatus.CONFLICT, "A pending request already exists")
            FriendRequestStatus.ACCEPTED ->
                throw ResponseStatusException(HttpStatus.CONFLICT, "Users are already friends")
            FriendRequestStatus.REJECTED -> {
                existing.sender = sender
                existing.receiver = receiver
                existing.status = FriendRequestStatus.PENDING
                friendRequestRepository.save(existing)
            }
            null -> try {
                friendRequestRepository.saveAndFlush(
                    FriendRequest(sender = sender, receiver = receiver, pairKey = pairKey)
                )
            } catch (_: DataIntegrityViolationException) {
                throw ResponseStatusException(HttpStatus.CONFLICT, "A request between these users already exists")
            }
        }
    }

    @Transactional
    fun acceptFriendRequest(requestId: Int, userId: Int) {
        val request = pendingRequest(requestId)
        if (request.receiver.id != userId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "No rights to accept this request")
        }
        request.status = FriendRequestStatus.ACCEPTED
        friendRequestRepository.save(request)
    }

    @Transactional
    fun rejectFriendRequest(requestId: Int, userId: Int) {
        val request = pendingRequest(requestId)
        if (request.receiver.id != userId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "No rights to reject this request")
        }
        request.status = FriendRequestStatus.REJECTED
        friendRequestRepository.save(request)
    }

    @Transactional
    fun cancelFriendRequest(requestId: Int, userId: Int) {
        val request = pendingRequest(requestId)
        if (request.sender.id != userId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "No rights to cancel this request")
        }
        friendRequestRepository.deleteById(requestId)
    }

    @Transactional
    fun removeFriend(userId: Int, friendId: Int) {
        val requests = friendRequestRepository.findBetweenUsers(userId, friendId).filter { it.status == FriendRequestStatus.ACCEPTED }
        if (requests.isEmpty()) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Friendship not found")
        }
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

    fun searchUsers(query: String, currentUserId: Int): List<User> = repository.searchUsers(query, currentUserId)

    private fun pendingRequest(requestId: Int): FriendRequest {
        val request = friendRequestRepository.findById(requestId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found")
        }
        if (request.status != FriendRequestStatus.PENDING) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Friend request is no longer pending")
        }
        return request
    }
}
