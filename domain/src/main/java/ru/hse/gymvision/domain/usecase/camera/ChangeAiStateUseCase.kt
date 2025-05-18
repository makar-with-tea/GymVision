package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.repos.GlobalRepository

class ChangeAiStateUseCase(
    private val globalRepository: GlobalRepository
) {
    suspend fun execute(gymId: Int, cameraId: Int, isAiEnabled: Boolean) {
        globalRepository.changeAiState(gymId, cameraId, isAiEnabled)
    }
}