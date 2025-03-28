package ru.hse.gymvision.domain.repos

interface SharedPrefRepository {
    suspend fun saveGymId(gymId: Int)
    suspend fun getGymId(): Int
    suspend fun saveUser(username: String)
    suspend fun getUser(): String?
    suspend fun clearInfo()
    suspend fun saveCameraIds(cameraIds: List<Int>)
    suspend fun getCameraIds(): List<Int>
}