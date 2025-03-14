package ru.hse.gymvision.domain.model

data class UserModel (
    val name: String,
    val surname: String,
    val login: String,
    val password: String
)