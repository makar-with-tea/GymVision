package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.repos.GlobalRepository

class CheckCameraAccessibilityUseCase(
    private val globalRepository: GlobalRepository
) {
    suspend fun execute(cameraId: Int): Boolean {
        return globalRepository.checkCameraAccessibility(cameraId)
    }
}
