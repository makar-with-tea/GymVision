package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.model.UserModel
import ru.hse.gymvision.domain.repos.GlobalRepository
import ru.hse.gymvision.domain.repos.SharedPrefRepository

class GetUserInfoUseCase(
    private val globalRepo: GlobalRepository,
    private val sharedPrefRepo: SharedPrefRepository
) {
    suspend fun execute(): UserModel? {
        val login = sharedPrefRepo.getUser()
        println("GetUserInfoUseCase: login = $login")
        return login?.let {
            globalRepo.getUserInfo(it)
        }
    }
}