package ru.hse.gymvision.ui.gymlist

import ru.hse.gymvision.domain.model.GymInfoModel

sealed class GymListState {
    data object Idle : GymListState()
    data class Main(
        val gyms: List<GymInfoModel> = emptyList()
    ) : GymListState()

    data object Loading : GymListState()
    data class Error(
        val message: String
    ) : GymListState()
}

sealed class GymListEvent {
    data object GetGymList : GymListEvent()
    data class SelectGym(val gymId: Int) : GymListEvent()
    data object Clear : GymListEvent()
}

sealed class GymListAction {
    data class NavigateToGym(val gymId: Int) : GymListAction()
}