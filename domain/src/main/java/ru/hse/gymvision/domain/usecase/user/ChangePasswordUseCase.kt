package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.GlobalRepository

class ChangePasswordUseCase(
    private val repo: GlobalRepository
) {
    suspend fun execute(login: String, newPassword: String) {
        repo.updateUser(login = login, password = newPassword)
    }
}