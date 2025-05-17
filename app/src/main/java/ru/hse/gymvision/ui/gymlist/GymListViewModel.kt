package ru.hse.gymvision.ui.gymlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.gymvision.domain.model.GymInfoModel
import ru.hse.gymvision.domain.usecase.gym.GetGymListUseCase

class GymListViewModel(
    private val getGymListUseCase: GetGymListUseCase
): ViewModel() {
    private val _state = MutableStateFlow<GymListState>(GymListState.Idle)
    val state = _state
    private val _action = MutableStateFlow<GymListAction?>(null)
    val action = _action

    fun obtainEvent(event: GymListEvent) {
        when (event) {
            is GymListEvent.GetGymList -> {
                getGymList()
            }
            is GymListEvent.SelectGym -> {
                selectGym(event.gymId)
            }
            is GymListEvent.Clear -> {
                clear()
            }
        }
    }

    private fun getGymList() {
        _state.value = GymListState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val gyms = getGymListUseCase.execute()
                withContext(Dispatchers.Main) {
                    _state.value = GymListState.Main(gyms)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("GymListViewModel", "Error getting gym list:", e)
                    _state.value = GymListState.Error(
                        "Не удалось соединиться с сервером. Проверьте подключение к интернету."
                    )
                }
            }
        }
    }

    private fun selectGym(gymId: Int) {
        _action.value = GymListAction.NavigateToGym(gymId)
    }

    private fun clear() {
        _state.value = GymListState.Idle
        _action.value = null
    }
}