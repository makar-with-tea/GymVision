package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.repos.GlobalRepository

class CheckCameraAccessibilityUseCase(
    private val repo: GlobalRepository
) {
    suspend fun execute(gymId: Int, cameraId: Int): Boolean {
        return repo.checkCameraAccessibility(gymId, cameraId)
    }
}