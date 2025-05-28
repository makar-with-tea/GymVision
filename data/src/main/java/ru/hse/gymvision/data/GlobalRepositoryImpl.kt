package ru.hse.gymvision.data

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import retrofit2.HttpException
import ru.hse.gymvision.data.api.GlobalApiService
import ru.hse.gymvision.data.model.CameraInfoDTO
import ru.hse.gymvision.data.model.LoginRequestDTO
import ru.hse.gymvision.data.model.RegisterRequestDTO
import ru.hse.gymvision.data.model.RotateInfoDTO
import ru.hse.gymvision.data.model.UserCheckPasswordDTO
import ru.hse.gymvision.data.model.ZoomInfoDTO
import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.CameraZoom
import ru.hse.gymvision.domain.exception.CameraInUseException
import ru.hse.gymvision.domain.exception.InvalidCredentialsException
import ru.hse.gymvision.domain.exception.LoginAlreadyInUseException
import ru.hse.gymvision.domain.model.ClickableCameraModel
import ru.hse.gymvision.domain.model.ClickableTrainerModel
import ru.hse.gymvision.domain.model.GymInfoModel
import ru.hse.gymvision.domain.model.GymSchemeModel
import ru.hse.gymvision.domain.model.TokenModel
import ru.hse.gymvision.domain.model.UserModel
import ru.hse.gymvision.domain.repos.GlobalRepository
import kotlin.random.Random

const val TAG = "GlobalRepositoryImpl"

class GlobalRepositoryImpl(
    private val apiService: GlobalApiService,
) : GlobalRepository {
    override suspend fun getGymList(): List<GymInfoModel> {
        try {
            return apiService.getGymList().map { gym ->
                GymInfoModel(
                    id = gym.id,
                    name = gym.name,
                    address = gym.address,
                    image = gym.image?.let { Base64.decode(it, Base64.DEFAULT) }
                )
            }
        } catch (e: HttpException) {
            Log.e(TAG, "Error fetching gym list: ${e.code()} - ${e.message()}")
            throw e
        }
    }

    override suspend fun getGymScheme(id: Int): GymSchemeModel? {
        try {
            return apiService.getGymScheme(id).let {
                it?.let { scheme ->
                    Log.d(TAG, "getGymScheme: ${scheme.clickableCameras}, ${scheme.clickableTrainers}, ${scheme.name}")
                    GymSchemeModel(
                        id = id,
                        scheme = Base64.decode(scheme.image, Base64.DEFAULT),
                        name = scheme.name,
                        clickableTrainerModels = scheme.clickableTrainers.map { trainer ->
                            ClickableTrainerModel(
                                id = trainer.id,
                                name = trainer.name,
                                description = trainer.description,
                                xPercent = trainer.xPercent,
                                yPercent = trainer.yPercent,
                                widthPercent = trainer.widthPercent,
                                heightPercent = trainer.heightPercent
                            )
                        },
                        clickableCameraModels = scheme.clickableCameras.map { camera ->
                            ClickableCameraModel(
                                id = camera.id,
                                xPercent = camera.xPercent,
                                yPercent = camera.yPercent
                            )
                        }
                    )
                }
            }
        } catch (e: HttpException) {
            if (e.code() == 404)
                return null
            throw e
        }
    }

    override suspend fun getUserInfo(login: String): UserModel? {
        try {
            return apiService.getUserInfo(login)?.let { user ->
                UserModel(
                    name = user.name,
                    surname = user.surname,
                    email = user.email,
                    login = user.login
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user info: ${e.message}")
            if (e is HttpException && e.code() == 404) {
                return null // User not found
            }
            throw e // Re-throw other exceptions
        }
    }

    override suspend fun login(login: String, password: String): TokenModel {
        try {
            apiService.login(LoginRequestDTO(login, password)).let { response ->
                Log.d(TAG, "login response: $response")
                return TokenModel(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken
                )
            }
        } catch (e: HttpException) {
            Log.d(TAG, "login error: ${e.code()} - ${e.message()}")
            if (e.code() == 401) {
                throw InvalidCredentialsException()
            }
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error during login: $e")
            throw e
        }
    }

    override suspend fun register(
        name: String,
        surname: String,
        email: String,
        login: String,
        password: String
    ): TokenModel {
        try {
            apiService.register(RegisterRequestDTO(name, surname, email, login, password))
                .let { response ->
                    return TokenModel(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken
                    )
                }
        } catch (e: HttpException) {
            if (e.code() == 400 && e.message() == "Login already in use") {
                throw LoginAlreadyInUseException()
            }
            throw e
        }
    }

    override suspend fun updateUser(name: String?, surname: String?, login: String, password: String?) {
        try {
            apiService.updateUser(
                login = login,
                name = name,
                surname = surname,
                password = password
            )
        } catch (e: HttpException) {
            Log.e(TAG, "Error updating user: ${e.code()} - ${e.message()}")
            throw e
        }
    }

    override suspend fun deleteUser(login: String) {
        try {
            apiService.deleteUser(login)
        } catch (e: HttpException) {
            Log.e(TAG, "Error deleting user: ${e.code()} - ${e.message()}")
            throw e
        }
    }

    override suspend fun startStream(cameraId: Int, aiEnabled: Boolean): String {
        try {
            apiService.startStream(
                CameraInfoDTO(
                    cameraId = cameraId,
                    aiEnabled = aiEnabled,
                )
            ).let { response ->
                Log.d(TAG, "startStream response: $response")
                return response.streamUrl
            }
        } catch (e: HttpException) {
            Log.e(TAG, "Error starting stream: ${e.code()} - ${e.message()}")
            if (e.code() == 409) {
                throw CameraInUseException()
            }
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error during startStream: $e")
            throw e
        }
    }

    @SuppressLint("ImplicitSamInstance")
    override suspend fun stopStream(cameraId: Int) {
        try {
            apiService.stopStream(
                CameraInfoDTO(
                    cameraId = cameraId,
                    aiEnabled = false
                )
            )
        } catch (e: HttpException) {
            Log.e(TAG, "Error stopping stream: ${e.code()} - ${e.message()}")
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error during stopStream: $e")
            throw e
        }
    }

    override suspend fun moveCamera(cameraId: Int, rotateX: Float, rotateY: Float) {
        try {
            apiService.moveCamera(
                RotateInfoDTO(
                    cameraId = cameraId,
                    rotateX = rotateX,
                    rotateY = rotateY
                )
            )
        } catch (e: HttpException) {
            Log.e(TAG, "Error moving camera: ${e.code()} - ${e.message()}")
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error during moveCamera: $e")
            throw e
        }
    }

    override suspend fun stopMove(cameraId: Int) {
        try {
            apiService.stopMove(
                CameraInfoDTO(
                    cameraId = cameraId,
                    aiEnabled = false
                )
            )
        } catch (e: HttpException) {
            Log.e(TAG, "Error stopping camera movement: ${e.code()} - ${e.message()}")
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error during stopMove: $e")
            throw e
        }
    }

    override suspend fun zoomCamera(cameraId: Int, zoomLevel: Float) {
        try {
            apiService.zoomCamera(
                ZoomInfoDTO(
                    cameraId = cameraId,
                    zoomLevel = zoomLevel
                )
            )
        } catch (e: HttpException) {
            Log.e(TAG, "Error zooming camera: ${e.code()} - ${e.message()}")
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error during zoomCamera: $e")
            throw e
        }
    }

    override suspend fun stopZoom(cameraId: Int) {
        try {
            apiService.stopZoom(
                CameraInfoDTO(
                    cameraId = cameraId,
                    aiEnabled = false
                )
            )
        } catch (e: HttpException) {
            Log.e(TAG, "Error stopping camera zoom: ${e.code()} - ${e.message()}")
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error during stopZoom: $e")
            throw e
        }
    }

    @SuppressLint("ImplicitSamInstance")
    override suspend fun checkCameraAccessibility(cameraId: Int): Boolean {
        try {
            apiService.startStream(
                CameraInfoDTO(
                    cameraId = cameraId,
                    aiEnabled = false
                )
            )
            apiService.stopStream(
                CameraInfoDTO(
                    cameraId = cameraId,
                    aiEnabled = false
                )
            )
            return true
        } catch (e: HttpException) {
            Log.e(TAG, "Error checking camera accessibility: ${e.code()} - ${e.message()}")
            if (e.code() == 409) {
                return false // Camera is in use
            }
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error during checkCameraAccessibility: $e")
            throw e
        }
    }

    override suspend fun checkPassword(login: String, password: String): Boolean {
        val response = apiService.checkPassword(
                UserCheckPasswordDTO(
                    login = login,
                    password = password
                )
            )
        return response["success"] ?: false
    }
}
