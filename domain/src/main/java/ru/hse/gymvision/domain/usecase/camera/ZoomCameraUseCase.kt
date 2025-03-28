package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.CameraZoom
import ru.hse.gymvision.domain.repos.LocalRepository

class ZoomCameraUseCase(
    private val repo: LocalRepository
) {
    suspend fun execute(cameraId: Int, direction: CameraZoom) {
    }
}