package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.GlobalRepository

class CheckPasswordUseCase(
    private val globalRepository: GlobalRepository
) {
    suspend fun execute(login: String, password: String): Boolean {
        return globalRepository.checkPassword(login, password)
    }
}