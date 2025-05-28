package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.CameraZoom
import ru.hse.gymvision.domain.repos.GlobalRepository

class ZoomCameraUseCase(
    private val globalRepository: GlobalRepository
) {
    suspend fun execute(cameraId: Int, direction: CameraZoom) {
        if (direction == CameraZoom.STOP) {
            globalRepository.stopZoom(cameraId)
            return
        }

        globalRepository.zoomCamera(
            cameraId,
            when (direction) {
                CameraZoom.IN -> 1f
                CameraZoom.OUT -> -1f
                else -> 0f
            }
        )
    }
}
