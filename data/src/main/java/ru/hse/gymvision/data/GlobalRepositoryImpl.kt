package ru.hse.gymvision.data

import android.annotation.SuppressLint
import android.util.Base64
import retrofit2.HttpException
import ru.hse.gymvision.data.api.GlobalApiService
import ru.hse.gymvision.data.model.CameraInfoDTO
import ru.hse.gymvision.data.model.LoginRequestDTO
import ru.hse.gymvision.data.model.RegisterRequestDTO
import ru.hse.gymvision.data.model.RotateInfoDTO
import ru.hse.gymvision.data.model.UserCheckPasswordDTO
import ru.hse.gymvision.data.model.ZoomInfoDTO
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


class GlobalRepositoryImpl(
    private val apiService: GlobalApiService,
) : GlobalRepository {
    override suspend fun getGymList(): List<GymInfoModel> {
            return apiService.getGymList().map { gym ->
                GymInfoModel(
                    id = gym.id,
                    name = gym.name,
                    address = gym.address,
                    image = gym.image?.let { Base64.decode(it, Base64.DEFAULT) }
                )
            }
    }

    override suspend fun getGymScheme(id: Int): GymSchemeModel? {
        try {
            return apiService.getGymScheme(id).let {
                it?.let { scheme ->
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
                return null // Gym scheme not found
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
            if (e is HttpException && e.code() == 404) {
                return null // User not found
            }
            throw e // Re-throw other exceptions
        }
    }

    override suspend fun login(login: String, password: String): TokenModel {
        try {
            apiService.login(LoginRequestDTO(login, password)).let { response ->
                return TokenModel(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken
                )
            }
        } catch (e: HttpException) {
            if (e.code() == 401) {
                throw InvalidCredentialsException()
            }
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

    override suspend fun updateUser(
        name: String?,
        surname: String?,
        email: String?,
        login: String,
        password: String?
    ) {
        apiService.updateUser(
            login = login,
            name = name,
            surname = surname,
            email = email,
            password = password
        )
    }

    override suspend fun deleteUser(login: String) {
        apiService.deleteUser(login)
    }

    override suspend fun startStream(cameraId: Int, aiEnabled: Boolean): String {
        try {
            apiService.startStream(
                CameraInfoDTO(
                    cameraId = cameraId,
                    aiEnabled = aiEnabled,
                )
            ).let { response ->
                return response.streamUrl
            }
        } catch (e: HttpException) {
            if (e.code() == 409) {
                throw CameraInUseException()
            }
            throw e
        }
    }

    @SuppressLint("ImplicitSamInstance")
    override suspend fun stopStream(cameraId: Int) {
        apiService.stopStream(
            CameraInfoDTO(
                cameraId = cameraId,
                aiEnabled = false
            )
        )
    }

    override suspend fun moveCamera(cameraId: Int, rotateX: Float, rotateY: Float) {
        apiService.moveCamera(
            RotateInfoDTO(
                cameraId = cameraId,
                rotateX = rotateX,
                rotateY = rotateY
            )
        )
    }

    override suspend fun stopMove(cameraId: Int) {
        apiService.stopMove(
            CameraInfoDTO(
                cameraId = cameraId,
                aiEnabled = false
            )
        )
    }

    override suspend fun zoomCamera(cameraId: Int, zoomLevel: Float) {
        apiService.zoomCamera(
            ZoomInfoDTO(
                cameraId = cameraId,
                zoomLevel = zoomLevel
            )
        )
    }

    override suspend fun stopZoom(cameraId: Int) {
        apiService.stopZoom(
            CameraInfoDTO(
                cameraId = cameraId,
                aiEnabled = false
            )
        )
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
            if (e.code() == 409) {
                return false // Camera is in use
            }
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
