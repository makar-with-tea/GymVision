package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.GlobalRepository

class UpdateUserUseCase(
    private val globalRepository: GlobalRepository
) {
    suspend fun execute(name: String, surname: String, login: String, email: String? = null) {
        globalRepository.updateUser(name = name, surname = surname, login = login, email = email)
    }
}