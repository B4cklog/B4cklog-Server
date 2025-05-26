package com.rarmash.b4cklog_server.controller

import com.rarmash.b4cklog_server.model.platform.Platform
import com.rarmash.b4cklog_server.model.platform.PlatformDAO
import com.rarmash.b4cklog_server.service.AuthService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/platforms")
class PlatformController (
    private val platformDAO: PlatformDAO,
    private val authService: AuthService
) {
    @PostMapping("/add")
    fun addPlatform(
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody platform: Platform,
    ): Platform {
        val token = authHeader.removePrefix("Bearer ").trim()
        authService.checkAdmin(token)
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

    @DeleteMapping("/delete/{id}")
    fun deletePlatform(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable("id") id: Int
    ) {
        val token = authHeader.removePrefix("Bearer ").trim()
        authService.checkAdmin(token)
        return platformDAO.deletePlatform(id = id)
    }
}