package org.b4cklog.server.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class IGDBGame(
    val id: Int,
    val name: String,
    val summary: String? = null,
    @JsonProperty("cover")
    val cover: IGDBCover? = null,
    @JsonProperty("first_release_date")
    val firstReleaseDate: Long? = null,
    @JsonProperty("platforms")
    val platforms: List<IGDBPlatform>? = null
)

data class IGDBCover(
    val id: Int,
    val url: String
) {
    fun getCoverUrl(): String {
        val processedUrl = url.replace("t_thumb", "t_cover_big")
        return if (processedUrl.startsWith("//")) {
            "https:$processedUrl"
        } else {
            processedUrl
        }
    }
}

data class IGDBPlatform(
    val id: Int,
    val name: String
)

data class IGDBTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("expires_in")
    val expiresIn: Int,
    @JsonProperty("token_type")
    val tokenType: String
) 