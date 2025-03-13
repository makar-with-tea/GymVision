package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.SharedPrefRepository

class LogoutUseCase(
    private val repo: SharedPrefRepository
) {
    suspend fun execute() {
        repo.clearInfo()
    }
}