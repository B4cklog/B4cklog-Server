package org.b4cklog.server.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnore

data class IGDBGame(
    val id: Int,
    val name: String,
    val summary: String? = null,
    @JsonProperty("cover")
    val cover: IGDBCover? = null,
    @JsonProperty("first_release_date")
    val firstReleaseDate: Long? = null,
    @JsonProperty("platforms")
    val platforms: List<IGDBPlatform>? = null,
    @JsonProperty("genres")
    val genres: List<IGDBGenre>? = null,
    @JsonProperty("screenshots")
    val screenshots: List<IGDBScreenshot>? = null
)

data class IGDBCover(
    val id: Int,
    val url: String
) {
    val coverUrl: String
        get() = url.replace("t_thumb", "t_cover_big")
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

data class IGDBGenre(
    val id: Int,
    val name: String
)

data class IGDBScreenshot(
    val id: Int,
    val url: String
) 