package ru.hse.gymvision.domain.usecase.gym

import ru.hse.gymvision.domain.repos.SharedPrefRepository

class GetGymIdUseCase(
    private val repo: SharedPrefRepository
) {
    suspend fun execute(): Int {
        return repo.getGymId()
    }
}