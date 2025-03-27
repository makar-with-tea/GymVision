package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.repos.LocalRepository

class CheckCameraAccessibilityUseCase(
    private val repo: LocalRepository
) {
    suspend fun execute(cameraId: Int): Boolean {
        return repo.checkCameraAccessibility(cameraId)
    }
}