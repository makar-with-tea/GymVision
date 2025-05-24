package ru.hse.gymvision.ui.gymlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.gymvision.domain.usecase.gym.GetGymListUseCase

class GymListViewModel(
    private val getGymListUseCase: GetGymListUseCase,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
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
        viewModelScope.launch(dispatcherIO) {
            try {
                val gyms = getGymListUseCase.execute()
                withContext(dispatcherMain) {
                    _state.value = GymListState.Main(gyms)
                }
            } catch (e: Exception) {
                withContext(dispatcherMain) {
                    _state.value = GymListState.Error
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