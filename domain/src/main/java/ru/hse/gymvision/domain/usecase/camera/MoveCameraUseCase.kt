package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.repos.GlobalRepository

class MoveCameraUseCase(
    private val globalRepository: GlobalRepository
) {
    suspend fun execute(gymId: Int, cameraId: Int, direction: CameraMovement) {
        globalRepository.moveCamera(gymId, cameraId, direction)
    }
}