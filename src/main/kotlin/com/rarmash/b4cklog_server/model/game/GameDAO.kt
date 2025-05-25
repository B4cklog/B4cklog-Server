package com.rarmash.b4cklog_server.model.game

import org.springframework.stereotype.Service

@Service
class GameDAO (
    private val repository: GameRepository
){
    fun addGame(game: Game) = repository.save(game)

    fun getGame(id: Int) = repository.findById(id)

    fun getAllGames() = repository.findAll().toList()

    fun deleteGame(id: Int) = repository.deleteById(id)

    fun searchGamesByName(namePart: String) = repository.findByNameContainingIgnoreCase(namePart)
}