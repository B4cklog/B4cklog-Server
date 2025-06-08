package org.b4cklog.server.model.game

import org.b4cklog.server.model.platform.PlatformRepository
import org.springframework.stereotype.Service

@Service
class GameDAO (
    private val repository: GameRepository,
    private val platformRepository: PlatformRepository
){
    fun addGame(game: Game): Game {
        val resolvedPlatforms = game.platforms.mapNotNull { partial ->
            platformRepository.findById(partial.id).orElse(null)
        }.toMutableList()

        val gameToSave = game.copy(platforms = resolvedPlatforms)

        return repository.save(gameToSave)
    }

    fun updateGame(updatedGame: Game): Game {
        val existing = repository.findById(updatedGame.id)
            .orElseThrow { NoSuchElementException("Игра с id ${updatedGame.id} не найдена") }

        existing.name = updatedGame.name
        existing.summary = updatedGame.summary
        existing.cover = updatedGame.cover
        existing.releaseDate = updatedGame.releaseDate
        existing.platforms = updatedGame.platforms

        return repository.save(existing)
    }

    fun getGame(id: Int) = repository.findById(id)

    fun getAllGames() = repository.findAll().toList()

    fun deleteGame(id: Int) = repository.deleteById(id)

    fun searchGamesByName(namePart: String) = repository.findByNameContainingIgnoreCase(namePart)
}