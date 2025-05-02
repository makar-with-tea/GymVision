package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.repos.GlobalRepository

class RotateCameraUseCase(
    private val repo: GlobalRepository
) {
    suspend fun execute(gymId: Int, cameraId: Int, direction: CameraRotation) {
        repo.rotateCamera(gymId, cameraId, direction)
    }
}