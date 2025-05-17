package ru.hse.gymvision.ui.account

sealed class AccountState {
    data object Idle : AccountState()

    data class Main(
        val name: String = "",
        val surname: String = "",
        val login: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
    ) : AccountState()

    data class EditName(
        val name: String = "",
        val surname: String = "",
        val login: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        ) : AccountState()

    data class ChangePassword(
        val oldPassword: String = "",
        val newPassword: String = "",
        val oldPasswordVisibility: Boolean = false,
        val newPasswordRepeatVisibility: Boolean = false,
        val name: String = "",
        val surname: String = "",
        val login: String = "",
        val password: String = "",
        val isLoading: Boolean = false
    ) : AccountState()

    data object Loading : AccountState()

    data class Error(
        val message: String
    ) : AccountState()
}

sealed class AccountEvent {
    data object GetUserInfo: AccountEvent()
    data class SaveNameButtonClicked(
        val name: String,
        val surname: String
    ): AccountEvent()
    data class SavePasswordButtonClicked(
        val newPassword: String,
        val oldPassword: String,
        val realPassword: String
    ): AccountEvent()

    data object ShowOldPasswordButtonClicked : AccountEvent()
    data object ShowNewPasswordButtonClicked : AccountEvent()
    data object EditNameButtonClicked : AccountEvent()
    data object EditPasswordButtonClicked: AccountEvent()
    data class DeleteAccountButtonClicked(val login: String): AccountEvent()
    data object LogoutButtonClicked: AccountEvent()
    data object Clear: AccountEvent()
}

sealed class AccountAction {
    data object NavigateToAuthorization : AccountAction()
}