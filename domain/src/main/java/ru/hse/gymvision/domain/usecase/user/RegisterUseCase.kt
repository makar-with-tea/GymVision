package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.GlobalRepository
import ru.hse.gymvision.domain.repos.SharedPrefRepository

class RegisterUseCase(
    private val globalRepo: GlobalRepository,
    private val sharedPrefRepo: SharedPrefRepository
) {
    suspend fun execute(name: String, surname: String, login: String, password: String): Boolean {
        val res = globalRepo.register(name, surname, login, password)
        if (res) {
            sharedPrefRepo.saveUser(login)
        }
        return res
    }
}