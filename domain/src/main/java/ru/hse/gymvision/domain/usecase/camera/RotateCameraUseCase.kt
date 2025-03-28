package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.repos.LocalRepository

class RotateCameraUseCase(
    private val repo: LocalRepository
) {
    suspend fun execute(cameraId: Int, direction: CameraRotation) {
    }
}