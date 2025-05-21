package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.repos.SharedPrefRepository

class SaveCamerasUseCase(
    private val sharedPrefRepository: SharedPrefRepository
) {
    suspend fun execute(cameraIds: List<Int>, cameraLinks: List<String>) {
        sharedPrefRepository.saveCameras(cameraIds, cameraLinks)
    }
}