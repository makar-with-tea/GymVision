package ru.hse.gymvision.domain.usecase.db

import ru.hse.gymvision.domain.exampledata.gymListExample
import ru.hse.gymvision.domain.model.GymInfoModel

class GetGymListUseCase {
    companion object {
        fun execute(): List<GymInfoModel> {
            return gymListExample
        }
    }
}