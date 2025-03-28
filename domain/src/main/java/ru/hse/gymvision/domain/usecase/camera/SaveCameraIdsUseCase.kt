package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.repos.SharedPrefRepository

class SaveCameraIdsUseCase(
    private val repo: SharedPrefRepository
) {
    suspend fun execute(cameraIds: List<Int>) {
        repo.saveCameraIds(cameraIds)
    }
}