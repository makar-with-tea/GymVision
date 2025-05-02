package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.repos.SharedPrefRepository

class SaveCamerasUseCase(
    private val repo: SharedPrefRepository
) {
    suspend fun execute(cameraIds: List<Int>, cameraLinks: List<String>) {
        repo.saveCameras(cameraIds, cameraLinks)
    }
}