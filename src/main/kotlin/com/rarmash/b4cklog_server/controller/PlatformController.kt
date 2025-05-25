package com.rarmash.b4cklog_server.controller

import com.rarmash.b4cklog_server.model.platform.Platform
import com.rarmash.b4cklog_server.model.platform.PlatformDAO
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/plaforms")
class PlatformController (private val platformDAO: PlatformDAO) {
    @GetMapping("/add")
    fun addPlatform(@RequestBody platform: Platform): Platform {
        return platformDAO.addPlatform(platform)
    }

    @GetMapping("/get/{id}")
    fun getPlatform(@PathVariable("id") id: Int): Optional<Platform> {
        return platformDAO.getPlatform(id = id)
    }

    @GetMapping("/get/all")
    fun getAllPlatforms(): List<Platform> {
        return platformDAO.getAllPlatforms()
    }

    @GetMapping("/delete/{id}")
    fun deletePlatform(@PathVariable("id") id: Int) {
        return platformDAO.deletePlatform(id = id)
    }
}