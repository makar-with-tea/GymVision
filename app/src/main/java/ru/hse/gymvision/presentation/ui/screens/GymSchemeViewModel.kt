package ru.hse.gymvision.presentation.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.hse.gymvision.domain.model.GymSchemeModel
import ru.hse.gymvision.domain.usecase.db.GetGymSchemeUseCase

class GymSchemeViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _gymScheme = MutableStateFlow<GymSchemeModel?>(null)
    val gymScheme: StateFlow<GymSchemeModel?> = _gymScheme

    fun loadGymScheme(gymId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _gymScheme.value = GetGymSchemeUseCase.execute(gymId)
            _isLoading.value = false
        }
    }
}