package ru.hse.gymvision.ui.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.hse.gymvision.domain.usecase.user.ChangePasswordUseCase
import ru.hse.gymvision.domain.usecase.user.DeleteUserUseCase
import ru.hse.gymvision.domain.usecase.user.GetUserInfoUseCase
import ru.hse.gymvision.domain.usecase.user.LogoutUseCase
import ru.hse.gymvision.domain.usecase.user.UpdateUserUseCase

class AccountViewModel(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val logoutUseCase: LogoutUseCase
): ViewModel() {
    private val _state: MutableStateFlow<AccountState> = MutableStateFlow(AccountState.Idle)
    val state : MutableStateFlow<AccountState>
        get() = _state
    private val _action = MutableStateFlow<AccountAction?>(null)
    val action : MutableStateFlow<AccountAction?>
        get() = _action

    fun obtainEvent(event: AccountEvent) {
        when (event) {
            is AccountEvent.GetUserInfo -> {
                getUserInfo(event.id)
            }
            is AccountEvent.SaveNameButtonClicked -> {
                saveName(
                    event.name,
                    event.surname
                )
            }

            is AccountEvent.SavePasswordButtonClicked -> {
                savePassword(
                    event.newPassword
                )
            }

            is AccountEvent.ShowOldPasswordButtonClicked -> {
                showOldPassword()
            }

            is AccountEvent.ShowNewPasswordButtonClicked -> {
                showNewPassword()
            }

            is AccountEvent.EditNameButtonClicked -> {
                editName()
            }

            is AccountEvent.EditPasswordButtonClicked -> {
                editPassword()
            }

            is AccountEvent.DeleteAccountButtonClicked -> {
                deleteAccount()
            }

            is AccountEvent.LogoutButtonClicked -> {
                logout()
            }

            is AccountEvent.Clear -> {
                clear()
            }
        }
    }

    private fun getUserInfo(id: Int) {
        _state.value = AccountState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val userInfo = getUserInfoUseCase.execute(id)
            if (userInfo == null) {
                logout()
                _action.value = AccountAction.NavigateToAuthorization
                return@launch
            }
            withContext(Dispatchers.Main) {
                _state.value = AccountState.Main(
                    name = userInfo.name,
                    surname = userInfo.surname,
                    login = userInfo.login,
                    password = userInfo.password
                )
            }
        }
    }

    private fun saveName(name: String, surname: String) {
        if (_state.value !is AccountState.EditName) return
//        updateUserUseCase.execute()
        _state.value = AccountState.Main(
            name = name,
            surname = surname,
            login = (_state.value as AccountState.EditName).login,
            password = (_state.value as AccountState.EditName).password
        )
        Log.d("AccountViewModel", "saveName: state = ${_state.value}")
    }

    private fun savePassword(newPassword: String) {
        if (_state.value !is AccountState.ChangePassword) return
        _state.value = (_state.value as AccountState.ChangePassword).copy(
            isLoading = true
        )
        viewModelScope.launch(Dispatchers.IO) {
            changePasswordUseCase.execute(
                (_state.value as AccountState.ChangePassword).login,
                newPassword
            )
            withContext(Dispatchers.Main) {
                _state.value = AccountState.Main(
                    password = newPassword,
                    name = (_state.value as AccountState.ChangePassword).name,
                    surname = (_state.value as AccountState.ChangePassword).surname,
                    login = (_state.value as AccountState.ChangePassword).login
                )
            }
        }
    }

    private fun showOldPassword() {
        if (_state.value is AccountState.ChangePassword) {
            _state.value = (_state.value as AccountState.ChangePassword).copy(
                oldPasswordVisibility = !(_state.value as AccountState.ChangePassword).oldPasswordVisibility)
        }
    }

    private fun showNewPassword() {
        if (_state.value is AccountState.ChangePassword) {
            _state.value = (_state.value as AccountState.ChangePassword).copy(
                newPasswordRepeatVisibility = !(_state.value as AccountState.ChangePassword).newPasswordRepeatVisibility)
        }
    }

    private fun editName() {
        Log.d("AccountViewModel", "editName: state = ${_state.value}")
        if (_state.value !is AccountState.Main) {
            return
        }
        _state.value = AccountState.EditName(
            name = (_state.value as AccountState.Main).name,
            surname = (_state.value as AccountState.Main).surname,
            login = (_state.value as AccountState.Main).login,
            password = (_state.value as AccountState.Main).password
        )
        Log.d("AccountViewModel", "editName: state = ${_state.value}")
    }

    private fun editPassword() {
        if (_state.value !is AccountState.Main) return
        _state.value = AccountState.ChangePassword(
            oldPassword = "",
            newPassword = "",
            oldPasswordVisibility = false,
            newPasswordRepeatVisibility = false,
            name = (_state.value as AccountState.Main).name,
            surname = (_state.value as AccountState.Main).surname,
            login = (_state.value as AccountState.Main).login,
            password = (_state.value as AccountState.Main).password
        )
    }

    private fun deleteAccount() {
//        deleteUserUseCase.execute()
        _action.value = AccountAction.NavigateToAuthorization
    }

    private fun logout() {
//        logoutUseCase.execute()
        _action.value = AccountAction.NavigateToAuthorization
    }

    private fun clear() {
        _state.value = AccountState.Idle
        _action.value = null
    }
}