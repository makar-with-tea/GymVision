package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.repos.GlobalRepository

class RotateCameraUseCase(
    private val globalRepository: GlobalRepository
) {
    suspend fun execute(gymId: Int, cameraId: Int, direction: CameraRotation) {
        globalRepository.rotateCamera(gymId, cameraId, direction)
    }
}