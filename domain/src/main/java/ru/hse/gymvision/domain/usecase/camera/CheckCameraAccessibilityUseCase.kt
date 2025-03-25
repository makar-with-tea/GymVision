package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.repos.GlobalRepository
import kotlin.random.Random

class CheckCameraAccessibilityUseCase(
    private val repo: GlobalRepository
) {
    suspend fun execute(cameraId: Int): Boolean {
        return repo.checkCameraAccessibility(cameraId)
    }
}