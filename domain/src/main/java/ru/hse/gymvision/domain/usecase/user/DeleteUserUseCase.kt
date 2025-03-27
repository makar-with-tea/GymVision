package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.repos.GlobalRepository
import ru.hse.gymvision.domain.repos.SharedPrefRepository

class DeleteUserUseCase(
    private val globalRepo: GlobalRepository,
    private val sharedPrefRepo: SharedPrefRepository
) {
    suspend fun execute(login: String) {
        sharedPrefRepo.clearInfo()
        globalRepo.deleteUser(login)
    }
}