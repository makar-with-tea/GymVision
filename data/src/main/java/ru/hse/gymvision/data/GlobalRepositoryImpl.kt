package ru.hse.gymvision.data

import ru.hse.gymvision.domain.exampledata.gymListExample
import ru.hse.gymvision.domain.exampledata.gymSchemeExample
import ru.hse.gymvision.domain.exampledata.userExample
import ru.hse.gymvision.domain.model.GymInfoModel
import ru.hse.gymvision.domain.model.GymSchemeModel
import ru.hse.gymvision.domain.model.UserModel
import ru.hse.gymvision.domain.repos.GlobalRepository

class GlobalRepositoryImpl: GlobalRepository {
    override suspend fun getGymList(): List<GymInfoModel> {
        return gymListExample
    }

    override suspend fun getGymScheme(id: Int): GymSchemeModel? {
        return gymSchemeExample
    }

    override suspend fun getUserInfo(): UserModel? {
        return userExample
    }

    override suspend fun login(login: String, password: String): Boolean {
        return true
    }

    override suspend fun register(name: String, surname: String, login: String, password: String): Boolean {
        return true
    }

    override suspend fun updateUser(name: String?, surname: String?, login: String, password: String?) {
        userExample = userExample.copy(
            name = name ?: userExample.name,
            surname = surname ?: userExample.surname,
            login = login,
            password = password ?: userExample.password
        )
    }

    override suspend fun logout() {
        // а надо ли оно мне
    }

    override suspend fun deleteUser(login: String) {
        // well.
    }
}