package org.b4cklog.server.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class IGDBConfig {
    
    private val logger = LoggerFactory.getLogger(IGDBConfig::class.java)
    
    @Value("\${igdb.client.id}")
    lateinit var clientId: String
    
    @Value("\${igdb.client.secret}")
    lateinit var clientSecret: String
    
    @Bean
    fun igdbWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl("https://api.igdb.com/v4")
            .build()
    }
    
    @Bean
    fun twitchWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl("https://id.twitch.tv/oauth2")
            .build()
    }
} 