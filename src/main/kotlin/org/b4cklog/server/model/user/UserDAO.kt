package org.b4cklog.server.model.user

import org.b4cklog.server.model.game.Game
import org.b4cklog.server.model.game.GameListType
import org.b4cklog.server.model.game.GameRepository
import org.b4cklog.server.util.HashUtils
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class UserDAO(
    private val repository: UserRepository,
    private val gameRepository: GameRepository
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

        // Сначала удаляем игру из всех списков
        removeGameFromAllLists(userId, gameId)

        // Добавляем игру в нужный список
        val listType = when (listName) {
            "wantToPlay" -> GameListType.WANT_TO_PLAY
            "playing" -> GameListType.PLAYING
            "played" -> GameListType.PLAYED
            "completed" -> GameListType.COMPLETED
            "completed100" -> GameListType.COMPLETED_100
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректное имя списка")
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
        val user = getUser(userId).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден") }

        if (repository.findByEmail(newEmail) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Этот email уже используется")
        }

        user.email = newEmail
        saveUser(user)
    }

    @Transactional
    fun updatePassword(userId: Int, newPassword: String) {
        val user = getUser(userId).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден") }

        user.password = HashUtils.sha256(newPassword)
        saveUser(user)
    }
}