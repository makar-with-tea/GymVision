package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.GlobalRepository

class LoginUseCase(
    private val repo: GlobalRepository
) {
    suspend fun execute(login: String, password: String): Boolean {
        return repo.login(login, password)
    }
}