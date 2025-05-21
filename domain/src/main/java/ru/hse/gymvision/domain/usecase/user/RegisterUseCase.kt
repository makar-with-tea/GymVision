package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.GlobalRepository
import ru.hse.gymvision.domain.repos.SharedPrefRepository

class RegisterUseCase(
    private val globalRepository: GlobalRepository,
    private val sharedPrefRepository: SharedPrefRepository
) {
    suspend fun execute(name: String, surname: String, login: String, password: String): Boolean {
        val res = globalRepository.register(name, surname, login, password)
        if (res) {
            sharedPrefRepository.saveUser(login)
        }
        return res
    }
}