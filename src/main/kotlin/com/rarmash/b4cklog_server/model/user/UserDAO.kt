package com.rarmash.b4cklog_server.model.user

import com.rarmash.b4cklog_server.model.game.Game
import com.rarmash.b4cklog_server.util.HashUtils
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserDAO(
    private val repository: UserRepository
) {
    fun addUser(user: User) = repository.save(user)

    fun getUser(id: Int) = repository.findById(id)

    fun getUserByUsername(username: String) = repository.findByUsername(username)

    fun getAllUsers() = repository.findAll().toList()

    fun deleteUser(id: Int) = repository.deleteById(id)

    fun saveUser(user: User) = repository.save(user)

    fun addGameToList(userId: Int, game: Game, listName: String) {
        val user = getUser(userId).orElseThrow { RuntimeException("User not found") }

        // Сначала удаляем игру из всех списков
        removeGameFromAllLists(user, game)

        // Добавляем игру в нужный список
        when (listName) {
            "wantToPlay" -> user.backlogWantToPlay.add(game)
            "playing" -> user.backlogPlaying.add(game)
            "played" -> user.backlogPlayed.add(game)
            "completed" -> user.backlogCompleted.add(game)
            "completed100" -> user.backlogCompleted100.add(game)
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректное имя списка")
        }

        saveUser(user)
    }

    private fun removeGameFromAllLists(user: User, game: Game) {
        user.backlogWantToPlay.remove(game)
        user.backlogPlaying.remove(game)
        user.backlogPlayed.remove(game)
        user.backlogCompleted.remove(game)
        user.backlogCompleted100.remove(game)
    }

    fun removeGameFromAllLists(userId: Int, game: Game) {
        val user = repository.findById(userId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден") }

        user.backlogWantToPlay.remove(game)
        user.backlogPlaying.remove(game)
        user.backlogPlayed.remove(game)
        user.backlogCompleted.remove(game)
        user.backlogCompleted100.remove(game)

        repository.save(user)
    }

    fun updateEmail(userId: Int, newEmail: String) {
        val user = getUser(userId).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден") }

        if (repository.findByEmail(newEmail) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Этот email уже используется")
        }

        user.email = newEmail
        saveUser(user)
    }

    fun updatePassword(userId: Int, newPassword: String) {
        val user = getUser(userId).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден") }

        user.password = HashUtils.sha256(newPassword)
        saveUser(user)
    }

}