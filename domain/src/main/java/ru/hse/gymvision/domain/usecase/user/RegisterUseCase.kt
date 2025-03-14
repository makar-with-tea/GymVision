package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.GlobalRepository

class RegisterUseCase(
    private val repo: GlobalRepository
) {
    suspend fun execute(name: String, surname: String, login: String, password: String) {
        repo.register(name, surname, login, password)
    }
}