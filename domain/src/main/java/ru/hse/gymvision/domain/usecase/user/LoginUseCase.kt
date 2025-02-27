package ru.hse.gymvision.domain.usecase.user

class LoginUseCase {
    companion object {
        suspend fun execute(login: String, password: String): Boolean {
            return true
        }
    }
}