package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.CameraZoom
import ru.hse.gymvision.domain.repos.GlobalRepository

class ZoomCameraUseCase(
    private val repo: GlobalRepository
) {
    suspend fun execute(gymId: Int, cameraId: Int, direction: CameraZoom) {
        repo.zoomCamera(gymId, cameraId, direction)
    }
}