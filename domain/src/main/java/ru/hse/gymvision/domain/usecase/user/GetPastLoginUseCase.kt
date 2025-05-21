package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.SharedPrefRepository

class GetPastLoginUseCase(
    private val sharedPrefRepository: SharedPrefRepository
) {
    suspend fun execute(): String? {
        return sharedPrefRepository.getUser()
    }
}