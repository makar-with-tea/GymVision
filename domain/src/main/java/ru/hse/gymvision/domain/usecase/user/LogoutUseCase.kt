package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.SharedPrefRepository

class LogoutUseCase(
    private val sharedPrefRepository: SharedPrefRepository
) {
    suspend fun execute() {
        sharedPrefRepository.clearInfo()
    }
}