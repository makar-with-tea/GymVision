package ru.hse.gymvision.ui.gymscheme

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.gymvision.domain.model.GymSchemeModel
import ru.hse.gymvision.domain.usecase.db.GetGymSchemeUseCase
import ru.hse.gymvision.domain.usecase.user.GetPastLoginUseCase
import ru.hse.gymvision.ui.authorization.AuthorizationAction
import ru.hse.gymvision.ui.authorization.AuthorizationState

class GymSchemeViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _gymScheme = MutableStateFlow<GymSchemeModel?>(null)
    val gymScheme: StateFlow<GymSchemeModel?> = _gymScheme

    fun loadGymScheme(gymId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _gymScheme.value = GetGymSchemeUseCase().execute(gymId)
            _isLoading.value = false
        }
    }

//    private fun checkPastLogin() {
//        _state.value = AuthorizationState.Loading
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                val username = GetPastLoginUseCase().execute()
//                if (username != null) {
//                    _action.value = AuthorizationAction.NavigateToGymList
//                } else {
//                    _state.value = AuthorizationState.Main()
//                    Log.d("AuthorizationViewModel", "CheckPastLogin")
//                }
//            }
//        }
//    }
}