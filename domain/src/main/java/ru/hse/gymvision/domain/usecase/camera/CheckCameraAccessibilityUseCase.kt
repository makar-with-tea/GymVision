package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.repos.GlobalRepository

class CheckCameraAccessibilityUseCase(
    private val globalRepository: GlobalRepository
) {
    suspend fun execute(gymId: Int, cameraId: Int): Boolean {
        return globalRepository.checkCameraAccessibility(gymId, cameraId)
    }
}