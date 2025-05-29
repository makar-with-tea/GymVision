package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.GlobalRepository
import ru.hse.gymvision.domain.repos.SharedPrefRepository

class RegisterUseCase(
    private val globalRepository: GlobalRepository,
    private val sharedPrefRepository: SharedPrefRepository
) {
    suspend fun execute(name: String, surname: String, email: String, login: String, password: String) {
        val res = globalRepository.register(name, surname, email, login, password)
        sharedPrefRepository.saveUser(login)
        sharedPrefRepository.saveToken(res.accessToken)
        sharedPrefRepository.saveRefreshToken(res.refreshToken)
    }
}
