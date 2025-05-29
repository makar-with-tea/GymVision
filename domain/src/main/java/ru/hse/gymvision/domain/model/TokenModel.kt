package ru.hse.gymvision.domain.model

data class TokenModel(
    val accessToken: String = "",
    val refreshToken: String = ""
)