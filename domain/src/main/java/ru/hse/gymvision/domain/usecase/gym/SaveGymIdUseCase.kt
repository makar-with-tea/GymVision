package ru.hse.gymvision.domain.usecase.gym

import ru.hse.gymvision.domain.repos.SharedPrefRepository

class SaveGymIdUseCase(
    private val sharedPrefRepository: SharedPrefRepository
) {
    suspend fun execute(gymId: Int) {
        sharedPrefRepository.saveGymId(gymId)
    }
}