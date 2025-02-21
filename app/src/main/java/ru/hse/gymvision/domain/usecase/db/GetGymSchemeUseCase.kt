package ru.hse.gymvision.domain.usecase.db

import ru.hse.gymvision.domain.exampledata.gymSchemeExample
import ru.hse.gymvision.domain.model.GymSchemeModel
import java.lang.Thread.sleep

class GetGymSchemeUseCase {
    companion object {
        suspend fun execute(id: Int): GymSchemeModel {
            sleep(1000)
            return gymSchemeExample
        }
    }
}