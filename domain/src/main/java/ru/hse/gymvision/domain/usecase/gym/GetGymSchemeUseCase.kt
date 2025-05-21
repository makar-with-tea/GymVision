package ru.hse.gymvision.domain.usecase.gym

import ru.hse.gymvision.domain.model.GymSchemeModel
import ru.hse.gymvision.domain.repos.GlobalRepository
import java.lang.Thread.sleep

class GetGymSchemeUseCase(
    private val globalRepository: GlobalRepository
) {
    suspend fun execute(id: Int): GymSchemeModel? {
            sleep(1000)
            return globalRepository.getGymScheme(id)
    }
}