package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.repos.GlobalRepository

class GetNewCameraLinkUseCase(
    private val globalRepository: GlobalRepository
) {
    suspend fun execute(gymId: Int, cameraId: Int): String {
        return globalRepository.getCameraLink(gymId, cameraId)
    }
}