package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.CameraZoom
import ru.hse.gymvision.domain.repos.GlobalRepository

class ZoomCameraUseCase(
    private val globalRepository: GlobalRepository
) {
    suspend fun execute(gymId: Int, cameraId: Int, direction: CameraZoom) {
        globalRepository.zoomCamera(gymId, cameraId, direction)
    }
}