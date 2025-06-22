package org.b4cklog.server.service

import org.b4cklog.server.config.IGDBConfig
import org.b4cklog.server.dto.IGDBGame
import org.b4cklog.server.dto.IGDBTokenResponse
import org.b4cklog.server.model.platform.Platform
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import org.b4cklog.server.dto.IGDBCover
import org.b4cklog.server.dto.IGDBScreenshot

@Service
class IGDBService(
    private val igdbWebClient: WebClient,
    private val twitchWebClient: WebClient,
    private val igdbConfig: IGDBConfig
) {
    
    private val logger = LoggerFactory.getLogger(IGDBService::class.java)
    private var accessToken: String? = null
    private var tokenExpiry: Instant? = null
    
    suspend fun searchGames(query: String, limit: Int = 20): List<IGDBGame> {
        try {
            val token = getValidToken()
            logger.info("Searching games with query: '$query', token: ${token.take(10)}...")
            
            val igdbQuery = """
                search "${query}";
                fields name,summary,cover.url,first_release_date,platforms.name,genres.name,screenshots.url;
                limit $limit;
            """.trimIndent()
            
            logger.info("IGDB Query: $igdbQuery")
            
            val igdbGames: List<IGDBGame> = igdbWebClient.post()
                .uri("/games")
                .header("Client-ID", igdbConfig.clientId)
                .header("Authorization", "Bearer $token")
                .bodyValue(igdbQuery)
                .retrieve()
                .awaitBody()

            logger.info("Found ${igdbGames.size} games from IGDB")
            igdbGames.forEach { game ->
                logger.info("Found game: ${game.name} (ID: ${game.id})")
            }
            
            return igdbGames.map { game ->
                game.copy(
                    cover = game.cover?.let { 
                        it.copy(url = if (it.url.startsWith("//")) 
                            "https:${it.url}".replace("t_thumb", "t_cover_big") 
                        else 
                            it.url.replace("t_thumb", "t_cover_big")
                        )
                    },
                    screenshots = game.screenshots?.map { s -> s.copy(url = if (s.url.startsWith("//")) "https:${s.url}" else s.url) }
                )
            }
        } catch (e: Exception) {
            logger.error("Error searching games: ${e.message}", e)
            return emptyList()
        }
    }
    
    suspend fun getGameById(id: Int): IGDBGame? {
        try {
            val token = getValidToken()
            logger.info("Getting game by ID: $id")
            
            val igdbQuery = """
                where id = $id;
                fields name,summary,cover.url,first_release_date,platforms.name,genres.name,screenshots.url;
            """.trimIndent()
            
            val igdbGames: List<IGDBGame> = igdbWebClient.post()
                .uri("/games")
                .header("Client-ID", igdbConfig.clientId)
                .header("Authorization", "Bearer $token")
                .bodyValue(igdbQuery)
                .retrieve()
                .awaitBody()
            
            logger.info("Found ${igdbGames.size} games for ID $id")
            return igdbGames.firstOrNull()?.let { game ->
                game.copy(
                    cover = game.cover?.let { 
                        it.copy(url = if (it.url.startsWith("//")) 
                            "https:${it.url}".replace("t_thumb", "t_cover_big") 
                        else 
                            it.url.replace("t_thumb", "t_cover_big")
                        )
                    },
                    screenshots = game.screenshots?.map { s -> s.copy(url = if (s.url.startsWith("//")) "https:${s.url}" else s.url) }
                )
            }
        } catch (e: Exception) {
            logger.error("Error getting game by ID $id: ${e.message}", e)
            return null
        }
    }
    
    suspend fun getLatestGames(limit: Int = 10): List<IGDBGame> {
        try {
            val token = getValidToken()
            logger.info("Getting latest games, limit: $limit")
            
            val igdbQuery = """
                where first_release_date != null & first_release_date < ${System.currentTimeMillis() / 1000};
                sort first_release_date desc;
                fields name,summary,cover.url,first_release_date,platforms.name,genres.name,screenshots.url;
                limit $limit;
            """.trimIndent()
            
            val igdbGames: List<IGDBGame> = igdbWebClient.post()
                .uri("/games")
                .header("Client-ID", igdbConfig.clientId)
                .header("Authorization", "Bearer $token")
                .bodyValue(igdbQuery)
                .retrieve()
                .awaitBody()
            
            logger.info("Found ${igdbGames.size} latest games")
            return igdbGames.map { game ->
                game.copy(
                    cover = game.cover?.let { 
                        it.copy(url = if (it.url.startsWith("//")) 
                            "https:${it.url}".replace("t_thumb", "t_cover_big") 
                        else 
                            it.url.replace("t_thumb", "t_cover_big")
                        )
                    },
                    screenshots = game.screenshots?.map { s -> s.copy(url = if (s.url.startsWith("//")) "https:${s.url}" else s.url) }
                )
            }
        } catch (e: Exception) {
            logger.error("Error getting latest games: ${e.message}", e)
            return emptyList()
        }
    }
    
    suspend fun getPopularGames(limit: Int = 10): List<IGDBGame> {
        try {
            val token = getValidToken()
            logger.info("Getting popular games, limit: $limit")
            
            val igdbQuery = """
                where rating_count > 100;
                sort rating_count desc;
                fields name,summary,cover.url,first_release_date,platforms.name,genres.name,screenshots.url;
                limit $limit;
            """.trimIndent()
            
            val igdbGames: List<IGDBGame> = igdbWebClient.post()
                .uri("/games")
                .header("Client-ID", igdbConfig.clientId)
                .header("Authorization", "Bearer $token")
                .bodyValue(igdbQuery)
                .retrieve()
                .awaitBody()
            
            logger.info("Found ${igdbGames.size} popular games")
            return igdbGames.map { game ->
                game.copy(
                    cover = game.cover?.let { 
                        it.copy(url = if (it.url.startsWith("//")) 
                            "https:${it.url}".replace("t_thumb", "t_cover_big") 
                        else 
                            it.url.replace("t_thumb", "t_cover_big")
                        )
                    },
                    screenshots = game.screenshots?.map { s -> s.copy(url = if (s.url.startsWith("//")) "https:${s.url}" else s.url) }
                )
            }
        } catch (e: Exception) {
            logger.error("Error getting popular games: ${e.message}", e)
            return emptyList()
        }
    }
    
    suspend fun getAllGames(limit: Int = 50): List<IGDBGame> {
        try {
            val token = getValidToken()
            logger.info("Getting all games, limit: $limit")
            
            val igdbQuery = """
                fields name,summary,cover.url,first_release_date,platforms.name,genres.name,screenshots.url;
                limit $limit;
            """.trimIndent()
            
            val igdbGames: List<IGDBGame> = igdbWebClient.post()
                .uri("/games")
                .header("Client-ID", igdbConfig.clientId)
                .header("Authorization", "Bearer $token")
                .bodyValue(igdbQuery)
                .retrieve()
                .awaitBody()
            
            logger.info("Found ${igdbGames.size} games total")
            return igdbGames.map { game ->
                game.copy(
                    cover = game.cover?.let { 
                        it.copy(url = if (it.url.startsWith("//")) 
                            "https:${it.url}".replace("t_thumb", "t_cover_big") 
                        else 
                            it.url.replace("t_thumb", "t_cover_big")
                        )
                    },
                    screenshots = game.screenshots?.map { s -> s.copy(url = if (s.url.startsWith("//")) "https:${s.url}" else s.url) }
                )
            }
        } catch (e: Exception) {
            logger.error("Error getting all games: ${e.message}", e)
            return emptyList()
        }
    }
    
    private suspend fun getValidToken(): String {
        if (accessToken == null || tokenExpiry == null || Instant.now().isAfter(tokenExpiry!!)) {
            logger.info("Token expired or null, refreshing...")
            refreshToken()
        }
        logger.info("Using token: ${accessToken?.take(10)}...")
        return accessToken!!
    }
    
    private suspend fun refreshToken() {
        try {
            logger.info("Refreshing IGDB token...")
            logger.info("Client ID: ${igdbConfig.clientId}")
            logger.info("Client Secret: ${igdbConfig.clientSecret.take(5)}...")
            
            val tokenResponse: IGDBTokenResponse = twitchWebClient.post()
                .uri("/token?client_id=${igdbConfig.clientId}&client_secret=${igdbConfig.clientSecret}&grant_type=client_credentials")
                .retrieve()
                .awaitBody()
            
            accessToken = tokenResponse.accessToken
            tokenExpiry = Instant.now().plusSeconds(tokenResponse.expiresIn.toLong())
            
            logger.info("Token refreshed successfully, expires in ${tokenResponse.expiresIn} seconds")
        } catch (e: Exception) {
            logger.error("Error refreshing token: ${e.message}", e)
            throw e
        }
    }
} 