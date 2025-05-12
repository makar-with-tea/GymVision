package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.repos.SharedPrefRepository

class GetCameraLinksUseCase(
    private val sharedPrefRepository: SharedPrefRepository,
) {
    suspend fun execute(): List<String> {
        return sharedPrefRepository.getCameraLinks()
    }
}