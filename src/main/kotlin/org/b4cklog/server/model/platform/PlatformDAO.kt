package org.b4cklog.server.model.platform

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