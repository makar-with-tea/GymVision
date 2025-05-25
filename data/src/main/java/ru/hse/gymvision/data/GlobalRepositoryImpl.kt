package ru.hse.gymvision.data

import android.util.Base64
import android.util.Log
import retrofit2.HttpException
import ru.hse.gymvision.data.api.GlobalApiService
import ru.hse.gymvision.data.model.LoginRequestDTO
import ru.hse.gymvision.data.model.RegisterRequestDTO
import ru.hse.gymvision.data.model.UserCheckPasswordDTO
import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.CameraZoom
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
            Log.e("marathinks", "Error fetching gym list: ${e.code()} - ${e.message()}")
            throw e
        }
    }

    override suspend fun getGymScheme(id: Int): GymSchemeModel? {
        try {
            return apiService.getGymScheme(id).let {
                it?.let { scheme ->
                    Log.d("marathinks", "getGymScheme: ${scheme.clickableCameras}, ${scheme.clickableTrainers}, ${scheme.name}")
                    GymSchemeModel(
                        id = id,
                        image = Base64.decode(scheme.image, Base64.DEFAULT),
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
                    login = user.login
                )
            }
        } catch (e: Exception) {
            Log.e("GlobalRepository", "Error fetching user info: ${e.message}")
            if (e is HttpException && e.code() == 404) {
                return null // User not found
            }
            throw e // Re-throw other exceptions
        }
    }

    override suspend fun login(login: String, password: String): TokenModel {
        try {
            apiService.login(LoginRequestDTO(login, password)).let { response ->
                Log.d("marathinks", "login response: $response")
                return TokenModel(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken
                )
            }
        } catch (e: HttpException) {
            Log.d("marathinks", "login error: ${e.code()} - ${e.message()}")
            if (e.code() == 401) {
                throw InvalidCredentialsException()
            }
            throw e
        }
    }

    override suspend fun register(
        name: String,
        surname: String,
        login: String,
        password: String
    ): TokenModel {
        try {
            apiService.register(RegisterRequestDTO(name, surname, login, password))
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
            Log.e("GlobalRepository", "Error updating user: ${e.code()} - ${e.message()}")
            throw e
        }
    }

    override suspend fun deleteUser(login: String) {
        try {
            apiService.deleteUser(login)
        } catch (e: HttpException) {
            Log.e("GlobalRepository", "Error deleting user: ${e.code()} - ${e.message()}")
            throw e
        }
    }

    override suspend fun checkCameraAccessibility(gymId: Int, cameraId: Int): Boolean {
        // todo
        return Random.nextBoolean()
    }

    override suspend fun getCameraLink(gymId: Int, cameraId: Int): String {
        // todo
        return "https://media.geeksforgeeks.org/wp-content/uploads/20201217163353/Screenrecorder-2020-12-17-16-32-03-350.mp4"
    }

    override suspend fun moveCamera(gymId: Int, cameraId: Int, direction: CameraMovement) {
        // todo
        Log.d("GlobalRepository", "Moving camera $cameraId in gym $gymId to direction $direction")
    }

    override suspend fun rotateCamera(gymId: Int, cameraId: Int, direction: CameraRotation) {
        // todo
        Log.d("GlobalRepository", "Rotating camera $cameraId in gym $gymId to direction $direction")
    }

    override suspend fun zoomCamera(gymId: Int, cameraId: Int, direction: CameraZoom) {
        // todo
        Log.d("GlobalRepository", "Zooming camera $cameraId in gym $gymId to direction $direction")
    }

    override suspend fun changeAiState(gymId: Int, cameraId: Int, isAiEnabled: Boolean) {
        // todo
        Log.d("GlobalRepository", "Changing AI state for camera $cameraId in gym $gymId to $isAiEnabled")
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