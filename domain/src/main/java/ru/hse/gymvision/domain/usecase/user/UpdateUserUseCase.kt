package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.GlobalRepository

class UpdateUserUseCase(
    private val repo: GlobalRepository
) {
    suspend fun execute(name: String, surname: String, login: String) {
        repo.updateUser(name = name, surname = surname, login = login)
    }
}