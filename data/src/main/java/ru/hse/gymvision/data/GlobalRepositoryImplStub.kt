package ru.hse.gymvision.data

import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.CameraZoom
import ru.hse.gymvision.domain.exampledata.gymListExample
import ru.hse.gymvision.domain.exampledata.gymSchemeExample
import ru.hse.gymvision.domain.exampledata.userExample
import ru.hse.gymvision.domain.model.GymInfoModel
import ru.hse.gymvision.domain.model.GymSchemeModel
import ru.hse.gymvision.domain.model.TokenModel
import ru.hse.gymvision.domain.model.UserModel
import ru.hse.gymvision.domain.repos.GlobalRepository
import kotlin.random.Random

class GlobalRepositoryImplStub: GlobalRepository {
    override suspend fun getGymList(): List<GymInfoModel> {
        if (Random.nextBoolean())
            throw Exception("aaa")
        return gymListExample
    }

    override suspend fun getGymScheme(id: Int): GymSchemeModel? {
        if (Random.nextBoolean())
            throw Exception("get gym scheme repo")
        return gymSchemeExample
    }

    override suspend fun getUserInfo(login: String): UserModel? {
        if (Random.nextBoolean())
            throw Exception("get user repo")
        if (userExample.login == login) {
            return userExample
        }
        return null
    }

    override suspend fun login(login: String, password: String): TokenModel {
        if (Random.nextBoolean())
            throw Exception("aaa")
        if (userExample.login != login) {
            throw Exception("Login or password is incorrect")
        }
        return TokenModel()
    }

    override suspend fun register(
        name: String,
        surname: String,
        email: String,
        login: String,
        password: String
    ): TokenModel {
        userExample = userExample.copy(
            name = name,
            surname = surname,
            login = login,
        )
        return TokenModel()
    }

    override suspend fun updateUser(
        name: String?,
        surname: String?,
        login: String,
        password: String?
    ) {
        if (Random.nextBoolean())
            throw Exception("aaa")
        userExample = userExample.copy(
            name = name ?: userExample.name,
            surname = surname ?: userExample.surname,
            login = login,
        )
    }

    override suspend fun deleteUser(login: String) {
        if (Random.nextBoolean())
            throw Exception("aaa")
        userExample = UserModel(
            name = "",
            surname = "",
            email = "",
            login = ""
        )
    }

    override suspend fun checkCameraAccessibility(gymId: Int, cameraId: Int): Boolean {
        return Random.nextBoolean()
    }

    override suspend fun getCameraLink(gymId: Int, cameraId: Int): String {
        return "https://media.geeksforgeeks.org/wp-content/uploads/20201217163353/Screenrecorder-2020-12-17-16-32-03-350.mp4"
    }

    override suspend fun moveCamera(gymId: Int, cameraId: Int, direction: CameraMovement) {
    }

    override suspend fun rotateCamera(gymId: Int, cameraId: Int, direction: CameraRotation) {
    }

    override suspend fun zoomCamera(gymId: Int, cameraId: Int, direction: CameraZoom) {
    }

    override suspend fun changeAiState(gymId: Int, cameraId: Int, isAiEnabled: Boolean) {
    }

    override suspend fun checkPassword(login: String, password: String): Boolean {
        return true
    }
}