package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.repos.GlobalRepository

class GetNewCameraLinkUseCase(
    private val repo: GlobalRepository
) {
    suspend fun execute(gymId: Int, cameraId: Int): String {
        return repo.getCameraLink(gymId, cameraId)
    }
}