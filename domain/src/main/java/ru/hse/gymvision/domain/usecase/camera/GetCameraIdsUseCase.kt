package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.repos.SharedPrefRepository

class GetCameraIdsUseCase(
    private val sharedPrefRepository: SharedPrefRepository
) {
    suspend fun execute(): List<Int> {
        return sharedPrefRepository.getCameraIds()
    }
}