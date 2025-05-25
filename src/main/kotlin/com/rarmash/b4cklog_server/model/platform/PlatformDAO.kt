package com.rarmash.b4cklog_server.model.platform

import com.rarmash.b4cklog_server.model.game.Game
import com.rarmash.b4cklog_server.model.game.GameRepository
import org.springframework.stereotype.Service

@Service
class PlatformDAO (
    private val repository: PlatformRepository
){
    fun addPlatform(platform: Platform) = repository.save(platform)

    fun getPlatform(id: Int) = repository.findById(id)

    fun getAllPlatforms() = repository.findAll().toList()

    fun deletePlatform(id: Int) = repository.deleteById(id)
}