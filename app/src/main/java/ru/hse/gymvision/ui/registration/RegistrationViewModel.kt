package ru.hse.gymvision.ui.registration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.gymvision.domain.usecase.user.GetPastLoginUseCase
import ru.hse.gymvision.domain.usecase.user.RegisterUseCase
import ru.hse.gymvision.ui.authorization.AuthorizationAction
import ru.hse.gymvision.ui.authorization.AuthorizationEvent
import ru.hse.gymvision.ui.authorization.AuthorizationState

class RegistrationViewModel(
    val registerUseCase: RegisterUseCase
): ViewModel() {
    private val _state: MutableStateFlow<RegistrationState> = MutableStateFlow(RegistrationState.Main(
        login = "",
        password = "",
        passwordRepeat = "",
        passwordVisibility = false,
        passwordRepeatVisibility = false,
        isError = false,
        loading = false
    ))
    val state : StateFlow<RegistrationState>
        get() = _state
    private val _action = MutableStateFlow<RegistrationAction?>(null)
    val action : StateFlow<RegistrationAction?>
        get() = _action

    fun obtainEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.RegistrationButtonClicked -> {
                register(
                    event.name,
                    event.surname,
                    event.login,
                    event.password
                )
            }

            is RegistrationEvent.LoginButtonClicked -> {
                _state.value = RegistrationState.Main(
                    login = event.login,
                    password = event.password
                )
                _action.value = RegistrationAction.NavigateToAuthorization
            }

            is RegistrationEvent.ShowPasswordButtonClicked -> {
                changeVisibilityState()
            }

            is RegistrationEvent.ShowPasswordRepeatButtonClicked -> {
                changeVisibilityRepeatState()
            }

            is RegistrationEvent.Clear -> clear()
        }
    }

    private fun register(name: String, surname: String, login: String, password: String) {
//        registerUseCase.execute(name, surname, login, password)
        _action.value = RegistrationAction.NavigateToGymList
    }

    private fun clear() {
        _state.value = RegistrationState.Main()
        _action.value = null
    }

    private fun changeVisibilityState() {
        if (_state.value is RegistrationState.Main) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                passwordVisibility = !(_state.value as RegistrationState.Main).passwordVisibility)
        }
    }

    private fun changeVisibilityRepeatState() {
        if (_state.value is RegistrationState.Main) {
            _state.value = (_state.value as RegistrationState.Main).copy(
                passwordRepeatVisibility = !(_state.value as RegistrationState.Main).passwordRepeatVisibility)
        }
    }
}