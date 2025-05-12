package ru.hse.gymvision.domain.usecase.gym

import ru.hse.gymvision.domain.repos.SharedPrefRepository

class GetGymIdUseCase(
    private val sharedPrefRepository: SharedPrefRepository
) {
    suspend fun execute(): Int {
        return sharedPrefRepository.getGymId()
    }
}