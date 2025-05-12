package ru.hse.gymvision.data

import ru.hse.gymvision.data.api.GlobalApiService
import ru.hse.gymvision.data.model.CameraInfoDTO
import ru.hse.gymvision.data.model.UserDTO
import ru.hse.gymvision.domain.CameraMovement
import ru.hse.gymvision.domain.CameraRotation
import ru.hse.gymvision.domain.CameraZoom
import ru.hse.gymvision.domain.exampledata.userExample
import ru.hse.gymvision.domain.model.ClickableCamera
import ru.hse.gymvision.domain.model.ClickableTrainer
import ru.hse.gymvision.domain.model.GymInfoModel
import ru.hse.gymvision.domain.model.GymSchemeModel
import ru.hse.gymvision.domain.model.UserModel
import ru.hse.gymvision.domain.repos.GlobalRepository
import kotlin.random.Random

class GlobalRepositoryImpl(
    private val apiService: GlobalApiService
) : GlobalRepository {//    override suspend fun getGymList(): List<GymInfoModel> {
//        return gymListExample
//    }
//
//    override suspend fun getGymScheme(id: Int): GymSchemeModel? {
//        return gymSchemeExample
//    }
//
//    override suspend fun getUserInfo(login: String): UserModel? {
//        if (userExample.login == login) {
//            return userExample
//        }
//        return null
//    }
//
//    override suspend fun login(login: String, password: String): Boolean {
//        return userExample.login == login && userExample.password == password
//    }
//
//    override suspend fun register(name: String, surname: String, login: String, password: String): Boolean {
//        userExample = userExample.copy(
//            name = name,
//            surname = surname,
//            login = login,
//            password = password
//        )
//        return true
//    }
//
//    override suspend fun updateUser(name: String?, surname: String?, login: String, password: String?) {
//        userExample = userExample.copy(
//            name = name ?: userExample.name,
//            surname = surname ?: userExample.surname,
//            login = login,
//            password = password ?: userExample.password
//        )
//    }
//
//    override suspend fun deleteUser(login: String) {
//        userExample = UserModel(
//            name = "",
//            surname = "",
//            login = "",
//            password = ""
//        )
//    }

//    override suspend fun checkLoginAvailable(login: String): Boolean {
//        return userExample.login != login
//    }
//
//    override suspend fun checkCameraAccessibility(gymId: Int, cameraId: Int): Boolean {
//        return Random.nextBoolean()
//    }
//
//    override suspend fun getCameraLink(gymId: Int, cameraId: Int): String {
//        return "https://media.geeksforgeeks.org/wp-content/uploads/20201217163353/Screenrecorder-2020-12-17-16-32-03-350.mp4"
//    }

    override suspend fun getGymList(): List<GymInfoModel> {
        return apiService.getGymList().map {
            GymInfoModel(
                id = it.id,
                name = it.name,
                address = it.address,
                image = it.image ?: ByteArray(0)
            )
        }
    }

    override suspend fun getGymScheme(id: Int): GymSchemeModel? {
        return apiService.getGymScheme(id)?.let { gymSchemeDTO ->
            val clickableCameras = gymSchemeDTO.clickableCameraDTOS.map {
                ClickableCamera(
                    id = it.id,
                    xPercent = it.xPercent,
                    yPercent = it.yPercent
                )
            }
            val clickableTrainers = gymSchemeDTO.clickableTrainerDTOS.map {
                ClickableTrainer(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    xPercent = it.xPercent,
                    yPercent = it.yPercent,
                    widthPercent = it.widthPercent,
                    heightPercent = it.heightPercent
                )
            }

            GymSchemeModel(
                image = gymSchemeDTO.image,
                name = gymSchemeDTO.name,
                clickableCameras = clickableCameras,
                clickableTrainers = clickableTrainers,
                id = 0 // TODO: Fix this
            )
        }
    }

    override suspend fun getUserInfo(login: String): UserModel? {
        return apiService.getUserInfo(login)?.let {
            UserModel(
                name = it.name,
                surname = it.surname,
                login = it.login,
                password = it.password
            )
        } ?: run {
            null
        }
    }

    override suspend fun login(login: String, password: String): Boolean {
        val response = apiService.login(UserDTO("", "", login, password))
        return response["success"] ?: false
    }

    override suspend fun register(name: String, surname: String, login: String, password: String): Boolean {
        val response = apiService.register(UserDTO(name, surname, login, password))
        return response["success"] ?: false
    }

    override suspend fun updateUser(name: String?, surname: String?, login: String, password: String?) {
        apiService.updateUser(login, name, surname, password)
    }

    override suspend fun deleteUser(login: String) {
        apiService.deleteUser(login)
    }

    override suspend fun checkLoginAvailable(login: String): Boolean {
        val user = apiService.getUserInfo(login)
        return user == null
    }

    override suspend fun checkCameraAccessibility(gymId: Int, cameraId: Int): Boolean {
        val cameras = apiService.getCameras()
        return cameras.any { it.cameraId == cameraId }
    }

    override suspend fun getCameraLink(gymId: Int, cameraId: Int): String {
        val streamInfo = apiService.startStream(CameraInfoDTO(cameraId))
        return streamInfo.streamUrl
    }

    override suspend fun moveCamera(gymId: Int, cameraId: Int, direction: CameraMovement) {
    }

    override suspend fun rotateCamera(gymId: Int, cameraId: Int, direction: CameraRotation) {
    }

    override suspend fun zoomCamera(gymId: Int, cameraId: Int, direction: CameraZoom) {
    }
}