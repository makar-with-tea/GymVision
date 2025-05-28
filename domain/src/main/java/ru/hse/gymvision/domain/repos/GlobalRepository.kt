package ru.hse.gymvision.domain.repos

import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.CameraZoom
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
    suspend fun checkCameraAccessibility(gymId: Int, cameraId: Int): Boolean
    suspend fun getCameraLink(gymId: Int, cameraId: Int): String
    suspend fun moveCamera(gymId: Int, cameraId: Int, direction: CameraMovement)
    suspend fun rotateCamera(gymId: Int, cameraId: Int, direction: CameraRotation)
    suspend fun zoomCamera(gymId: Int, cameraId: Int, direction: CameraZoom)
    suspend fun changeAiState(gymId: Int, cameraId: Int, isAiEnabled: Boolean)
    suspend fun checkPassword(login: String, password: String): Boolean
}