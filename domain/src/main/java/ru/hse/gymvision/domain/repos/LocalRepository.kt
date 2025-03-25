package ru.hse.gymvision.domain.repos

interface LocalRepository {
    suspend fun checkCameraAccessibility(id: Int): Boolean
}