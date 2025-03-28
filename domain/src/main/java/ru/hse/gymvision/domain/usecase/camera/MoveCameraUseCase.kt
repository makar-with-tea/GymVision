package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.repos.LocalRepository

class MoveCameraUseCase(
    private val repo: LocalRepository
) {
    suspend fun execute(cameraId: Int, direction: CameraMovement) {
    }
}