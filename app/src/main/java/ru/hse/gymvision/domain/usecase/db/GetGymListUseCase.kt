package ru.hse.gymvision.domain.usecase.db

import ru.hse.gymvision.domain.exampledata.gymListExample
import ru.hse.gymvision.domain.model.GymModel

class GetGymListUseCase {
    companion object {
        fun execute(): List<GymModel> {
            return gymListExample
        }
    }
}