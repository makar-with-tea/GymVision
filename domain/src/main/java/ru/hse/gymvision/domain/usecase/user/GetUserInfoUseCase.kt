package ru.hse.gymvision.domain.usecase.user

import ru.hse.gymvision.domain.model.UserModel

class GetUserInfoUseCase {
    fun execute(id: Int): UserModel {
        return UserModel(
            name = "Иван",
            surname = "Иванов",
            login = "vanya100",
            password = "helpmeicantdothisanymore"
        )
    }
}