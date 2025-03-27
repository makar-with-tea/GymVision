package ru.hse.gymvision.domain.repos

import ru.hse.gymvision.domain.model.GymInfoModel
import ru.hse.gymvision.domain.model.GymSchemeModel
import ru.hse.gymvision.domain.model.UserModel

interface GlobalRepository {
    suspend fun getGymList(): List<GymInfoModel>
    suspend fun getGymScheme(id: Int): GymSchemeModel?
    suspend fun getUserInfo(login: String): UserModel?
    suspend fun login(login: String, password: String): Boolean
    suspend fun register(name: String, surname: String, login: String, password: String): Boolean
    suspend fun updateUser(name: String? = null, surname: String? = null, login: String, password: String? = null)
    suspend fun logout()
    suspend fun deleteUser(login: String)
}