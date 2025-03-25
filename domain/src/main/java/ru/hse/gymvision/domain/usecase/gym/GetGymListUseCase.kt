package ru.hse.gymvision.domain.usecase.gym

import ru.hse.gymvision.domain.model.GymInfoModel
import ru.hse.gymvision.domain.repos.GlobalRepository

class GetGymListUseCase(
    private val repo: GlobalRepository
) {
    suspend fun execute(): List<GymInfoModel> {
        return repo.getGymList()
    }
}