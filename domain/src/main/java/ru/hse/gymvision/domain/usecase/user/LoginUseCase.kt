package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.GlobalRepository
import ru.hse.gymvision.domain.repos.SharedPrefRepository

class LoginUseCase(
    private val globalRepo: GlobalRepository,
    private val sharedPrefRepo: SharedPrefRepository
) {
    suspend fun execute(login: String, password: String): Boolean {
        val res = globalRepo.login(login, password)
        if (res) {
            sharedPrefRepo.saveUser(login)
        }
        println("LoginUseCase: login = $login, password = $password, res = $res")
        return res
    }
}