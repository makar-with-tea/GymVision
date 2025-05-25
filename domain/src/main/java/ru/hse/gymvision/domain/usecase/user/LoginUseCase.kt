package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.GlobalRepository
import ru.hse.gymvision.domain.repos.SharedPrefRepository

class LoginUseCase(
    private val globalRepository: GlobalRepository,
    private val sharedPrefRepository: SharedPrefRepository
) {
    suspend fun execute(login: String, password: String) {
        val res = globalRepository.login(login, password)
        sharedPrefRepository.saveUser(login)
        sharedPrefRepository.saveToken(res.accessToken)
        sharedPrefRepository.saveRefreshToken(res.refreshToken)
        println("LoginUseCase: login = $login, password = $password, res = $res")
    }
}