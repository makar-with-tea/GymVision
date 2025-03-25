package ru.hse.gymvision.data

import ru.hse.gymvision.domain.repos.LocalRepository
import kotlin.random.Random

class LocalRepositoryImpl: LocalRepository {
    override suspend fun checkCameraAccessibility(id: Int): Boolean {
        return Random.nextBoolean()
    }
}