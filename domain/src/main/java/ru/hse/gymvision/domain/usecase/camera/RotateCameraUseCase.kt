package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.repos.GlobalRepository

class RotateCameraUseCase(
    private val globalRepository: GlobalRepository
) {
    suspend fun execute(cameraId: Int, direction: CameraRotation) {
        if (direction == CameraRotation.STOP) {
            globalRepository.stopMove(cameraId)
            return
        }

        globalRepository.moveCamera(
            cameraId,
            when (direction) {
                CameraRotation.LEFT -> -1f
                CameraRotation.RIGHT -> 1f
                else -> 0f
            },
            0f
        )
    }
}