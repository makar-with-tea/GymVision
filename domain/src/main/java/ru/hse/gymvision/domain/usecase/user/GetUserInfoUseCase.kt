package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.model.UserModel
import ru.hse.gymvision.domain.repos.GlobalRepository

class GetUserInfoUseCase(
    private val repo: GlobalRepository
) {
    suspend fun execute(id: Int): UserModel? {
        return repo.getUserInfo()
    }
}