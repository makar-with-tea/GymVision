package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.GlobalRepository

class CheckLoginAvailableUseCase(
    private val globalRepository: GlobalRepository
) {
    suspend fun execute(login: String): Boolean {
        return globalRepository.checkLoginAvailable(login)
    }
}