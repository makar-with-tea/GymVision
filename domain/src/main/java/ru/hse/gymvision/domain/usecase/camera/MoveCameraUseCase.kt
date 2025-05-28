package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.repos.GlobalRepository

class MoveCameraUseCase(
    private val globalRepository: GlobalRepository
) {
    suspend fun execute(cameraId: Int, direction: CameraMovement) {
        if (direction == CameraMovement.STOP) {
            globalRepository.stopMove(cameraId)
            return
        }

        globalRepository.moveCamera(
            cameraId,
            0f,
            when (direction) {
                CameraMovement.UP -> 1f
                CameraMovement.DOWN -> -1f
                else -> 0f
            }
        )
    }
}
