package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.model.UserModel
import ru.hse.gymvision.domain.repos.GlobalRepository
import ru.hse.gymvision.domain.repos.SharedPrefRepository

class GetUserInfoUseCase(
    private val globalRepository: GlobalRepository,
    private val sharedPrefRepository: SharedPrefRepository
) {
    suspend fun execute(): UserModel? {
        val login = sharedPrefRepository.getUser()
        println("GetUserInfoUseCase: login = $login")
        return login?.let {
            globalRepository.getUserInfo(it)
        }
    }
}