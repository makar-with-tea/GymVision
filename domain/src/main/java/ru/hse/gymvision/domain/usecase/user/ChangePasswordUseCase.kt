package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.GlobalRepository

class ChangePasswordUseCase(
    private val globalRepository: GlobalRepository
) {
    suspend fun execute(login: String, newPassword: String) {
        globalRepository.updateUser(login = login, password = newPassword)
    }
}