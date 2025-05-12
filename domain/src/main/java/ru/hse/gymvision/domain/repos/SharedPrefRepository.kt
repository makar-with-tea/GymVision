package ru.hse.gymvision.domain.repos

interface SharedPrefRepository {
    suspend fun saveGymId(gymId: Int)
    suspend fun getGymId(): Int
    suspend fun saveUser(username: String)
    suspend fun getUser(): String?
    suspend fun clearInfo()
    suspend fun saveCameras(cameraIds: List<Int>, cameraLinks: List<String>)
    suspend fun getCameraIds(): List<Int>
    suspend fun getCameraLinks(): List<String>
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
    fun saveRefreshToken(token: String)
    fun getRefreshToken(): String?
    fun clearRefreshToken()
}