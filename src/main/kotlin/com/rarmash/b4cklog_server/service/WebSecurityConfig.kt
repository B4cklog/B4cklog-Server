package com.rarmash.b4cklog_server.service

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class WebSecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeRequests()
            .anyRequest().permitAll()
//            .authorizeHttpRequests { auth ->
//                auth
//                    .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login").permitAll()
//                    .anyRequest().authenticated()
//            }
        return http.build()
    }
}