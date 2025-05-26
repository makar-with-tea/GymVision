package ru.hse.gymvision.domain.exception

class InvalidCredentialsException: Exception("Invalid credentials provided.") {
    override val message: String
        get() = "Invalid credentials provided. Please check your login and password."
}