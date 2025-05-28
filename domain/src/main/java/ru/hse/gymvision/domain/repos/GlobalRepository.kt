package ru.hse.gymvision.domain.repos

import ru.hse.gymvision.domain.model.GymInfoModel
import ru.hse.gymvision.domain.model.GymSchemeModel
import ru.hse.gymvision.domain.model.TokenModel
import ru.hse.gymvision.domain.model.UserModel

interface GlobalRepository {
    suspend fun getGymList(): List<GymInfoModel>
    suspend fun getGymScheme(id: Int): GymSchemeModel?
    suspend fun getUserInfo(login: String): UserModel?
    suspend fun login(login: String, password: String): TokenModel
    suspend fun register(name: String, surname: String, email: String, login: String, password: String): TokenModel
    suspend fun updateUser(name: String? = null, surname: String? = null, login: String, password: String? = null)
    suspend fun deleteUser(login: String)
    suspend fun startStream(cameraId: Int, aiEnabled: Boolean): String
    suspend fun stopStream(cameraId: Int)
    suspend fun moveCamera(cameraId: Int, rotateX: Float, rotateY: Float)
    suspend fun stopMove(cameraId: Int)
    suspend fun zoomCamera(cameraId: Int, zoomLevel: Float)
    suspend fun stopZoom(cameraId: Int)
    suspend fun checkPassword(login: String, password: String): Boolean
    suspend fun checkCameraAccessibility(cameraId: Int): Boolean
}