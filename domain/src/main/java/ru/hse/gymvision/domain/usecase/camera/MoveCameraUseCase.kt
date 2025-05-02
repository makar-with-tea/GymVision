package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.repos.GlobalRepository

class MoveCameraUseCase(
    private val repo: GlobalRepository
) {
    suspend fun execute(gymId: Int, cameraId: Int, direction: CameraMovement) {
        repo.moveCamera(gymId, cameraId, direction)
    }
}