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
                getUserInfo()
            }
            is AccountEvent.SaveNameButtonClicked -> {
                saveName(
                    event.name,
                    event.surname
                )
            }

            is AccountEvent.SavePasswordButtonClicked -> {
                savePassword(
                    event.newPassword, event.oldPassword, event.realPassword
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
                deleteAccount(event.login)
            }

            is AccountEvent.LogoutButtonClicked -> {
                logout()
            }

            is AccountEvent.Clear -> {
                clear()
            }
        }
    }

    private fun getUserInfo() {
        _state.value = AccountState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userInfo = getUserInfoUseCase.execute() ?: run {
                    withContext(Dispatchers.Main) {
                        _state.value = AccountState.Error(
                            error = AccountState.AccountError.ACCOUNT_NOT_FOUND
                        )
                    }
                    return@launch
                }
                Log.d("AccountViewModel", "getUserInfo: userInfo = $userInfo")
                withContext(Dispatchers.Main) {
                    _state.value = AccountState.Main(
                        name = userInfo.name,
                        surname = userInfo.surname,
                        login = userInfo.login,
                        password = userInfo.password
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("AccountViewModel", "getUserInfo: error = $e")
                    _state.value = AccountState.Error(
                        error = AccountState.AccountError.NETWORK
                    )
                }
            }
        }
    }

    private fun saveName(name: String, surname: String) {
        if (_state.value !is AccountState.EditName) return
        _state.value = (_state.value as AccountState.EditName).copy(
            isLoading = true
        )
            var isError = false
            if (name.length < 2 || name.length > 20) {
                _state.value = (_state.value as AccountState.EditName).copy(
                    nameError = AccountState.AccountError.NAME_LENGTH
                )
                isError = true
            }
            if (surname.length < 2 || surname.length > 20) {
                _state.value = (_state.value as AccountState.EditName).copy(
                    surnameError = AccountState.AccountError.SURNAME_LENGTH
                )
                isError = true
            }
            if (isError) {
                _state.value = (_state.value as AccountState.EditName).copy(
                    isLoading = false
                )
                return
            }
        viewModelScope.launch(Dispatchers.IO) {
        try {
                updateUserUseCase.execute(
                    name = name,
                    surname = surname,
                    login = (_state.value as AccountState.EditName).login,
                )
                _state.value = AccountState.Main(
                    name = name,
                    surname = surname,
                    login = (_state.value as AccountState.EditName).login,
                    password = (_state.value as AccountState.EditName).password
                )
                Log.d("AccountViewModel", "saveName: state = ${_state.value}")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.value = AccountState.EditName(
                        name = name,
                        surname = surname,
                        login = (_state.value as AccountState.EditName).login,
                        password = (_state.value as AccountState.EditName).password,
                        surnameError = AccountState.AccountError.NETWORK,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun savePassword(newPassword: String, oldPassword: String, realPassword: String) {
        if (_state.value !is AccountState.ChangePassword) return
        if (oldPassword != realPassword) {
            _state.value = (_state.value as AccountState.ChangePassword).copy(
                oldPasswordError = AccountState.AccountError.PASSWORD_INCORRECT
            )
            return
        }
        _state.value = (_state.value as AccountState.ChangePassword).copy(
            isLoading = true
        )
        viewModelScope.launch(Dispatchers.IO) {
            try {
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
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.value = (_state.value as AccountState.ChangePassword).copy(
                        isLoading = false,
                        newPasswordError = AccountState.AccountError.CHANGE_FAILED
                    )
                }
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
            name = (_state.value as AccountState.Main).name,
            surname = (_state.value as AccountState.Main).surname,
            login = (_state.value as AccountState.Main).login,
            password = (_state.value as AccountState.Main).password
        )
    }

    private fun deleteAccount(login: String) {
        if (_state.value !is AccountState.Main) return
        _state.value = AccountState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteUserUseCase.execute(login)
                withContext(Dispatchers.Main) {
                    _action.value = AccountAction.NavigateToAuthorization
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.value = AccountState.Error(
                        error = AccountState.AccountError.ACCOUNT_DELETE
                    )
                }
            }
        }
    }

    private fun logout() {
        _state.value = AccountState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                logoutUseCase.execute()
            } finally {
                withContext(Dispatchers.Main) {
                    _action.value = AccountAction.NavigateToAuthorization
                }
            }
        }
    }

    private fun clear() {
        _state.value = AccountState.Idle
        _action.value = null
        Log.d("AccountViewModel", "cleared")
    }
}