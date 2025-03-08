package ru.hse.gymvision.domain.usecase.camera

import kotlin.random.Random

class CheckCameraAccessibilityUseCase {
    fun execute(cameraId: Int): Boolean {
        return Random.nextBoolean()
    }
}