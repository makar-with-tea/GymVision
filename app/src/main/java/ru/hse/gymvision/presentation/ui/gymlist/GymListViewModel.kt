package ru.hse.gymvision.presentation.ui.gymlist

import androidx.lifecycle.ViewModel
import ru.hse.gymvision.domain.model.GymInfoModel
import ru.hse.gymvision.domain.usecase.db.GetGymListUseCase

class GymListViewModel: ViewModel() {
    fun getGymList(): List<GymInfoModel> {
        return GetGymListUseCase.execute()
    }
}