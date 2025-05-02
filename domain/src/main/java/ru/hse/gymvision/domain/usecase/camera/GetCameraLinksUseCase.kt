package ru.hse.gymvision.domain.usecase.camera

import ru.hse.gymvision.domain.repos.SharedPrefRepository

class GetCameraLinksUseCase(
    private val repo: SharedPrefRepository,
) {
    suspend fun execute(): List<String> {
        return repo.getCameraLinks()
    }
}