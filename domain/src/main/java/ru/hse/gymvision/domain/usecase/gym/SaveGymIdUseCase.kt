package ru.hse.gymvision.domain.usecase.gym

import ru.hse.gymvision.domain.repos.SharedPrefRepository

class SaveGymIdUseCase(
    private val repo: SharedPrefRepository
) {
    suspend fun execute(gymId: Int) {
        repo.saveGymId(gymId)
    }
}