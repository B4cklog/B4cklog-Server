package org.b4cklog.server.controller

import org.b4cklog.server.dto.IGDBGame
import org.b4cklog.server.service.IGDBService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/games")
class GameController (
    private val igdbService: IGDBService
) {
    @GetMapping("/get/{id}")
    suspend fun getGame(@PathVariable("id") id: Int): Optional<IGDBGame> {
        return igdbService.getGameById(id)?.let { Optional.of(it) } ?: Optional.empty()
    }

    @GetMapping("/get/all")
    suspend fun getAllGames(): List<IGDBGame> {
        return igdbService.getAllGames()
    }

    @GetMapping("/search")
    suspend fun searchGames(@RequestParam("q") query: String): List<IGDBGame> {
        return igdbService.searchGames(query)
    }

    @GetMapping("/latest")
    suspend fun getLatestGames(): List<IGDBGame> {
        return igdbService.getLatestGames()
    }

    @GetMapping("/popular")
    suspend fun getPopularGames(): List<IGDBGame> {
        return igdbService.getPopularGames()
    }
}