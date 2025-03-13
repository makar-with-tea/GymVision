package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.GlobalRepository

class DeleteUserUseCase(
    private val repo: GlobalRepository
) {
    suspend fun execute(login: String) {
        repo.deleteUser(login)
    }
}