package com.celi.mobile.shared

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequestDto(
    val username: String,
    val password: String,
    val rememberMe: Boolean = true
)

@Serializable
data class AuthResponseDto(
    val id_token: String
)

@Serializable
data class RegisterRequestDto(
    val login: String,
    val email: String,
    val password: String,
    val langKey: String = "es"
)

