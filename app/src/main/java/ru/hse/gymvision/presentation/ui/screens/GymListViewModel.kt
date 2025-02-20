package ru.hse.gymvision.presentation.ui.screens

import androidx.lifecycle.ViewModel
import ru.hse.gymvision.domain.model.GymModel
import ru.hse.gymvision.domain.usecase.db.GetGymListUseCase

class GymListViewModel: ViewModel() {
    fun getGymList(): List<GymModel> {
        return GetGymListUseCase.execute()
    }
}