package com.rarmash.b4cklog_server.model.token

import org.springframework.stereotype.Service

@Service
class TokenDAO(private val repository: TokenRepository) {

    fun saveToken(token: Token): Token = repository.save(token)

    fun getToken(tokenString: String): Token? = repository.findByToken(tokenString)

    fun deleteToken(tokenString: String) = repository.deleteByToken(tokenString)
}